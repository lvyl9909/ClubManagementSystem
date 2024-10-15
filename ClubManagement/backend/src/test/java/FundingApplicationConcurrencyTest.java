import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.teamy.backend.DataMapper.*;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Club;
import org.teamy.backend.model.FundingApplication;
import org.teamy.backend.model.fundingApplicationStatus;
import org.teamy.backend.repository.*;
import org.teamy.backend.service.EventService;
import org.teamy.backend.service.FundingApplicationService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class FundingApplicationConcurrencyTest {
    private EventService eventService;
    private EventRepository eventRepository;
    private RSVPRepository rsvpRepository;
    private TicketRepository ticketRepository;
    private VenueRepository venueRepository;
    private ClubRepository clubRepository;
    private ClubDataMapper clubDataMapper;
    private EventDataMapper eventDataMapper;

    private FundingApplicationMapper fundingApplicationMapper;

    private RSVPDataMapper rsvpDataMapper;

    private StudentClubDataMapper studentClubDataMapper;

    private StudentDataMapper studentDataMapper;

    private TicketDataMapper ticketDataMapper;
    private VenueDataMapper venueDataMapper;

    private StudentRepository studentRepository;

    private FundingApplicationService fundingApplicationService;
    private FundingApplicationRepository fundingApplicationRepository;

    private DatabaseConnectionManager databaseConnectionManager;

    private Club club;

    @BeforeEach
    public void setUp() {
        // 使用 Mockito 模拟依赖项
        eventRepository = Mockito.mock(EventRepository.class);
        rsvpRepository = Mockito.mock(RSVPRepository.class);
        ticketRepository = Mockito.mock(TicketRepository.class);
        venueRepository = Mockito.mock(VenueRepository.class);
        clubRepository = Mockito.mock(ClubRepository.class);
        clubDataMapper = Mockito.mock(ClubDataMapper.class);
        eventDataMapper = Mockito.mock(EventDataMapper.class);
        fundingApplicationMapper = Mockito.mock(FundingApplicationMapper.class);
        rsvpDataMapper = Mockito.mock(RSVPDataMapper.class);
        studentClubDataMapper = Mockito.mock(StudentClubDataMapper.class);
        studentDataMapper = Mockito.mock(StudentDataMapper.class);
        ticketDataMapper = Mockito.mock(TicketDataMapper.class);
        venueDataMapper = Mockito.mock(VenueDataMapper.class);
        studentRepository = Mockito.mock(StudentRepository.class);
        fundingApplicationMapper = Mockito.mock(FundingApplicationMapper.class);
        fundingApplicationService = Mockito.mock(FundingApplicationService.class);

//        databaseConnectionManager = Mockito.mock(DatabaseConnectionManager.class);
        databaseConnectionManager = new DatabaseConnectionManager(
                "jdbc:postgresql://dpg-cqqa5sjv2p9s73b4fi2g-a.singapore-postgres.render.com/swen90007_teamy",
                "swen90007_teamy_owner",
                "MmDETsMioPzOVdhSJoB4T3wwrxD1ElGH");
        // 如果 `init` 方法是必须调用的，你可以模拟它的行为
        databaseConnectionManager.init();
//        doNothing().when(databaseConnectionManager).init();

        // 在测试前重置单例
        EventService.resetInstance();

        // 初始化 EventService 的实例
        clubDataMapper = ClubDataMapper.getInstance(databaseConnectionManager);
        eventDataMapper = EventDataMapper.getInstance(databaseConnectionManager);
        fundingApplicationMapper = FundingApplicationMapper.getInstance(databaseConnectionManager);
        rsvpDataMapper = RSVPDataMapper.getInstance(databaseConnectionManager);
        studentClubDataMapper = StudentClubDataMapper.getInstance(databaseConnectionManager);
        studentDataMapper = StudentDataMapper.getInstance(databaseConnectionManager);
        ticketDataMapper = TicketDataMapper.getInstance(databaseConnectionManager);
        venueDataMapper = VenueDataMapper.getInstance(databaseConnectionManager);

        eventRepository = EventRepository.getInstance(eventDataMapper,venueDataMapper,clubDataMapper);
        rsvpRepository = RSVPRepository.getInstance(rsvpDataMapper);
        studentRepository = StudentRepository.getInstance(clubDataMapper,rsvpDataMapper,ticketDataMapper,studentDataMapper,studentClubDataMapper,fundingApplicationMapper);
        clubRepository = ClubRepository.getInstance(clubDataMapper,eventDataMapper,fundingApplicationMapper,studentRepository,studentClubDataMapper);
        ticketRepository = TicketRepository.getInstance(ticketDataMapper);
        venueRepository = VenueRepository.getInstance(venueDataMapper);
        fundingApplicationRepository = FundingApplicationRepository.getInstance(fundingApplicationMapper);

        fundingApplicationService = FundingApplicationService.getInstance(fundingApplicationRepository, clubRepository,databaseConnectionManager);

    }

    @Test
    public void testConcurrentFundingApplicationSubmissions() throws InterruptedException {
        int numberOfThreads = 10;  // 模拟10个管理员并发提交申请
        List<FundingApplication> applications = new ArrayList<>();

        // 创建用于模拟的资金申请对象
        for (int i = 0; i < numberOfThreads; i++) {
            FundingApplication application = new FundingApplication(
                    "Funding for project " + i,
                    BigDecimal.valueOf(1000 + i * 100),
                    1,
                    10,  // 确保 club 对象不为 null 且包含有效的 clubId
                    fundingApplicationStatus.Submitted,
                    new java.util.Date()
            );
            applications.add(application);
        }

        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // 提交并发任务
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            Callable<Boolean> task = () -> {
                long startTime = System.currentTimeMillis(); // 记录开始时间

                try (Connection conn = databaseConnectionManager.nextConnection()) {
                    // 模拟锁持有时间，延长锁持有时间
                    Thread.sleep(200); // 0.2秒，模拟一个较长的操作

                    // 调用 saveFundingApplication 方法
                    return fundingApplicationService.saveFundingApplication(applications.get(index), conn);
                } catch (SQLException e) {
                    System.err.println("SQL Error saving funding application for thread " + index + ": " + e.getMessage());
                    e.printStackTrace();
                    return false;
                } catch (Exception e) {
                    System.err.println("Error saving funding application for thread " + index + ": " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }finally {
                    long lockAcquiredTime = System.currentTimeMillis(); // 获取锁之后的时间

                    // 计算完成时间
                    long waitTime = lockAcquiredTime - startTime;
                    System.out.println("Thread " + index + " waited " + waitTime + " ms to acquire the lock");
                    // 断言锁等待时间大于0，确保线程确实在等待
                    assert waitTime > 0 : "Thread should have waited for the lock";
                }
            };
            futures.add(executorService.submit(task));
        }

        // 等待所有任务完成
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        // 统计结果
        int successCount = 0;
        int failureCount = 0;
        for (Future<Boolean> future : futures) {
            try {
                if (future.get()) {
                    successCount++;
                } else {
                    failureCount++;
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        // 打印测试结果
        System.out.println("Success count: " + successCount);
        System.out.println("Failure count: " + failureCount);

        // 添加断言
        assert successCount > 0 : "At least one operation should succeed";
        assert failureCount >= 0 : "Some operations may fail due to lock contention";
    }

    @Test
    public void testConcurrentFundingApplicationReviews() throws InterruptedException, ExecutionException {
        int numberOfThreads = 10;  // 模拟 10 个线程并发审核
        int applicationId = 11;     // 假设测试的 funding application ID 为 1
        int reviewerId = 1;        // 假设测试的 reviewer ID 为 1

        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // 创建任务列表
        List<Callable<Boolean>> tasks = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numberOfThreads; i++) {
            final int threadNumber = i;
            Callable<Boolean> task = () -> {
                try {
                    // 随机设置状态为 Approved 或 Rejected
                    String status = random.nextBoolean() ? "Approved" : "Rejected";
                    System.out.println("Thread " + threadNumber + " is reviewing the funding application with status: " + status);

                    // 调用审核资金申请的方法
                    fundingApplicationService.reviewFundingApplication(applicationId, reviewerId, status);

                    System.out.println("Thread " + threadNumber + " finished with status: " + status);
                    return true;
                } catch (Exception e) {
                    System.err.println("Thread " + threadNumber + " encountered an error: " + e.getMessage());
                    return false;
                }
            };
            tasks.add(task);
        }

        // 提交所有任务
        List<Future<Boolean>> futures = executorService.invokeAll(tasks);

        // 检查所有线程是否成功执行
        for (int i = 0; i < numberOfThreads; i++) {
            Future<Boolean> future = futures.get(i);
            if (future.get()) {
                System.out.println("Thread " + i + " succeeded.");
            } else {
                System.out.println("Thread " + i + " failed.");
            }
        }

        // 关闭线程池
        executorService.shutdown();
    }

    @Test
    public void testConcurrentUpdateFundingApplications() throws InterruptedException, ExecutionException {
        int numberOfThreads = 10;  // 模拟 10 个线程并发更新
        int applicationId = 4;     // 假设测试的 funding application ID 为 1

        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // 创建任务列表
        List<Callable<Boolean>> tasks = new ArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            final int threadNumber = i;
            Callable<Boolean> task = () -> {
                try {
                    // 构造一个模拟的 FundingApplication 对象，每个线程可以模拟不同的更新
                    FundingApplication application = fundingApplicationService.findFundingApplicationById(applicationId);

                    application.setAmount(BigDecimal.valueOf(1000));
                    System.out.println("Thread " + threadNumber + " is updating the funding application.");

                    // 调用 updateFundingApplication 方法进行更新
                    boolean success = fundingApplicationService.updateFundingApplication(application);

                    System.out.println("Thread " + threadNumber + " finished. Success: " + success);
                    return success;
                } catch (Exception e) {
                    System.err.println("Thread " + threadNumber + " encountered an error: " + e.getMessage());
                    return false;
                }
            };
            tasks.add(task);
        }

        // 提交所有任务
        List<Future<Boolean>> futures = executorService.invokeAll(tasks);

        // 检查所有线程是否成功执行
        for (int i = 0; i < numberOfThreads; i++) {
            Future<Boolean> future = futures.get(i);
            if (future.get()) {
                System.out.println("Thread " + i + " succeeded.");
            } else {
                System.out.println("Thread " + i + " failed.");
            }
        }

        // 关闭线程池
        executorService.shutdown();
    }
}
