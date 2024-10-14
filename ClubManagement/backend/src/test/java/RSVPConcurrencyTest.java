import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.teamy.backend.DataMapper.*;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.repository.*;
import org.teamy.backend.security.CustomUserDetailsService;
import org.teamy.backend.service.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class RSVPConcurrencyTest {

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

    private DatabaseConnectionManager databaseConnectionManager;

    @BeforeEach
    public void setUp() {
        databaseConnectionManager = new DatabaseConnectionManager(
                "jdbc:postgresql://dpg-cqqa5sjv2p9s73b4fi2g-a.singapore-postgres.render.com/swen90007_teamy",
                "swen90007_teamy_owner",
                "MmDETsMioPzOVdhSJoB4T3wwrxD1ElGH");
        // 如果 `init` 方法是必须调用的，你可以模拟它的行为
        databaseConnectionManager.init();

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
        eventService = Mockito.mock(EventService.class);

//        databaseConnectionManager = Mockito.mock(DatabaseConnectionManager.class);
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

        eventService = EventService.getInstance(eventRepository, rsvpRepository, ticketRepository, venueRepository, clubRepository, databaseConnectionManager);
    }

    @Test
    public void testConcurrentRSVPApplications() throws InterruptedException {
        int numberOfThreads = 3;  // 例如模拟 10 个用户并发申请
        int eventId = 1;  // 测试的 eventId
        int studentId = 7;  // 假设一个学生 ID
        int numTickets = 2;  // 每个学生申请 2 张票
        List<Integer> participates_id = Arrays.asList(7, 8);  // 参与者 ID 列表

        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        Random random = new Random();

        // 提交并发任务
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            final int index=i;
            final int currentStudentId = studentId + i;  // 模拟不同的学生 ID
            Callable<Boolean> task = () -> {
                long startTime = System.currentTimeMillis(); // 记录开始时间
                try {
                    // 调用 applyForRSVP 方法
                    eventService.applyForRSVP(eventId, currentStudentId, numTickets, participates_id,4);
                    return true;  // 成功
                } catch (Exception e) {
                    System.err.println("Error applying for RSVP: " + e.getMessage());
                    return false;  // 失败
                }finally {
                    long lockAcquiredTime = System.currentTimeMillis(); // 获取锁之后的时间

                    // 计算完成时间
                    long waitTime = lockAcquiredTime - startTime;
                    System.out.println("Thread " + index + " waited " + waitTime + " ms to acquire the lock");
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

        // 可以进一步断言成功和失败次数是否符合预期，例如：
        // assertTrue(successCount > 0);
        // assertTrue(failureCount > 0);
    }
}