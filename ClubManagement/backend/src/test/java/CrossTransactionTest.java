import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.teamy.backend.DataMapper.*;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Event;
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

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CrossTransactionTest {

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
        fundingApplicationService = Mockito.mock(FundingApplicationService.class);

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
        fundingApplicationRepository = FundingApplicationRepository.getInstance(fundingApplicationMapper);

        eventService = EventService.getInstance(eventRepository, rsvpRepository, ticketRepository, venueRepository, clubRepository, databaseConnectionManager);
        fundingApplicationService = FundingApplicationService.getInstance(fundingApplicationRepository, clubRepository,databaseConnectionManager);

    }

    @Test
    public void testConcurrentRSVPAndEventUpdates() throws InterruptedException {
        int numberOfRSVPThreads = 10;  // 模拟5个学生并发申请
        int numberOfEventUpdateThreads = 1;  // 模拟5个管理员并发更新事件
        int eventId = 4;  // 假设测试的 eventId
        int studentId = 7;  // 假设一个初始学生 ID
        int numTickets = 2;  // 每个学生申请 2 张票
        List<Integer> participatesId = List.of(7, 8);  // 参与者 ID 列表

        // 创建线程池，分别用于 RSVP 和 Event Update
        ExecutorService rsvpExecutor = Executors.newFixedThreadPool(numberOfRSVPThreads);
        ExecutorService eventUpdateExecutor = Executors.newFixedThreadPool(numberOfEventUpdateThreads);

        List<Future<Boolean>> rsvpFutures = new ArrayList<>();
        List<Future<Boolean>> eventUpdateFutures = new ArrayList<>();

        Random random = new Random();

        // 提交并发 RSVP 任务
        for (int i = 0; i < numberOfRSVPThreads/2; i++) {
            final int currentStudentId = studentId + i;  // 模拟不同的学生 ID
            Callable<Boolean> rsvpTask = () -> {
                try {
                    eventService.applyForRSVP(eventId, currentStudentId, numTickets, participatesId, 5);  // 申请 RSVP
                    return true;
                } catch (Exception e) {
                    System.err.println("RSVP Error: " + e.getMessage());
                    return false;
                }
            };
            rsvpFutures.add(rsvpExecutor.submit(rsvpTask));
        }

        // 提交并发 Event Update 任务
        for (int i = 0; i < numberOfEventUpdateThreads; i++) {
            Callable<Boolean> eventUpdateTask = () -> {
                try {
                    // 获取并更新事件
                    Event event = eventService.getEventById(eventId);
                    event.setCapacity((int) 4);  // 每个线程更新不同的容量
                    return eventService.updateEvent(event);  // 更新事件
                } catch (Exception e) {
                    System.err.println("Event Update Error: " + e.getMessage());
                    return false;
                }
            };
            eventUpdateFutures.add(eventUpdateExecutor.submit(eventUpdateTask));
        }
        // 提交并发 RSVP 任务
        for (int i = numberOfRSVPThreads/2; i < numberOfRSVPThreads; i++) {
            final int currentStudentId = studentId + i;  // 模拟不同的学生 ID
            Callable<Boolean> rsvpTask = () -> {
                try {
                    eventService.applyForRSVP(eventId, currentStudentId, numTickets, participatesId, 5);  // 申请 RSVP
                    return true;
                } catch (Exception e) {
                    System.err.println("RSVP Error: " + e.getMessage());
                    return false;
                }
            };
            rsvpFutures.add(rsvpExecutor.submit(rsvpTask));
        }

        // 等待所有 RSVP 任务完成
        rsvpExecutor.shutdown();
        rsvpExecutor.awaitTermination(1, TimeUnit.MINUTES);

        // 等待所有 Event Update 任务完成
        eventUpdateExecutor.shutdown();
        eventUpdateExecutor.awaitTermination(1, TimeUnit.MINUTES);

        // 统计 RSVP 任务结果
        int rsvpSuccessCount = 0;
        int rsvpFailureCount = 0;
        for (Future<Boolean> future : rsvpFutures) {
            try {
                if (future.get()) {
                    rsvpSuccessCount++;
                } else {
                    rsvpFailureCount++;
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        // 统计 Event Update 任务结果
        int eventUpdateSuccessCount = 0;
        int eventUpdateFailureCount = 0;
        for (Future<Boolean> future : eventUpdateFutures) {
            try {
                if (future.get()) {
                    eventUpdateSuccessCount++;
                } else {
                    eventUpdateFailureCount++;
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        // 打印测试结果
        System.out.println("RSVP Success count: " + rsvpSuccessCount);
        System.out.println("RSVP Failure count: " + rsvpFailureCount);
        System.out.println("Event Update Success count: " + eventUpdateSuccessCount);
        System.out.println("Event Update Failure count: " + eventUpdateFailureCount);

        // 断言结果
        assertTrue(rsvpSuccessCount > 0);  // 确保至少有一些 RSVP 成功
        assertTrue(eventUpdateSuccessCount > 0);  // 确保至少有一些事件更新成功
        assertTrue(rsvpFailureCount > 0 || eventUpdateFailureCount > 0);  // 确保并发冲突导致的失败发生
    }

    @Test
    public void testInterleavedFundingApplicationOperations() throws InterruptedException {
        int applicationId = 8;    // 假设测试的 funding application ID
        int reviewerId = 1;       // 假设测试的 reviewer ID

        // 创建线程池，大小为 2，分别用于 review 和 update 操作
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // 定义 review 操作
        Callable<Boolean> reviewTask = () -> {
            long startTime = System.currentTimeMillis(); // 记录开始时间
            String status = "Approved";  // 假设审核通过
            try {
                System.out.println("Thread-Review is reviewing the funding application with status: " + status);
                fundingApplicationService.reviewFundingApplication(applicationId, reviewerId, status);
                return true;  // 成功
            } catch (Exception e) {
                System.err.println("Error in Thread-Review: " + e.getMessage());
                return false;
            } finally {
                long endTime = System.currentTimeMillis(); // 获取结束时间
                long waitTime = endTime - startTime;
                System.out.println("Thread-Review waited " + waitTime + " ms to complete the operation.");
            }
        };

        // 定义 update 操作
        Callable<Boolean> updateTask = () -> {
            long startTime = System.currentTimeMillis(); // 记录开始时间
            try {
                System.out.println("Thread-Update is updating the funding application.");
                FundingApplication application = fundingApplicationService.findFundingApplicationById(applicationId);
                application.setAmount(BigDecimal.valueOf(1000 + Thread.currentThread().getId()));  // 更新金额
                fundingApplicationService.updateFundingApplication(application);
                return true;  // 成功
            } catch (SQLException e) {
                System.err.println("SQL Error in Thread-Update: " + e.getMessage());
                return false;
            } catch (Exception e) {
                System.err.println("Error in Thread-Update: " + e.getMessage());
                return false;
            } finally {
                long endTime = System.currentTimeMillis(); // 获取结束时间
                long waitTime = endTime - startTime;
                System.out.println("Thread-Update waited " + waitTime + " ms to complete the operation.");
            }
        };

        // 提交 review 和 update 操作任务
        Future<Boolean> reviewFuture = executorService.submit(reviewTask);
        Future<Boolean> updateFuture = executorService.submit(updateTask);

        // 等待所有任务完成
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        // 统计结果
        int successCount = 0;
        int failureCount = 0;

        // 检查 review 操作结果
        try {
            if (reviewFuture.get()) {
                successCount++;
            } else {
                failureCount++;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // 检查 update 操作结果
        try {
            if (updateFuture.get()) {
                successCount++;
            } else {
                failureCount++;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // 打印测试结果
        System.out.println("Success count: " + successCount);
        System.out.println("Failure count: " + failureCount);

        // 添加断言
        assertTrue(successCount > 0, "At least one operation should succeed");
        assertTrue(failureCount >= 0, "Some operations may fail due to lock contention");
    }
}