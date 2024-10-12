import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.teamy.backend.DataMapper.*;
import org.teamy.backend.config.DatabaseConnectionManager;
import org.teamy.backend.model.Event;
import org.teamy.backend.repository.*;
import org.teamy.backend.service.EventService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class EventServiceConcurrencyTest {

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
    public void testConcurrentEventUpdates() throws InterruptedException {
        int numberOfThreads = 10;  // 模拟10个线程同时更新事件
        int eventId = 47;  // 假设要更新的事件ID
        int clubId =30;
        int venueId =11;


        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // 提交并发任务
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            Callable<Boolean> task = () -> {
                try {
                    // 模拟不同的更新操作
                    Event event = eventService.getEventById(eventId);
//                    event.setTitle("Updated Event Title " + Thread.currentThread().getId());
                    event.setCapacity((int) (100 + Thread.currentThread().getId()));  // 每个线程试图设置不同的容量

                    return eventService.updateEvent(event);  // 调用 updateEvent 方法
                } catch (Exception e) {
                    System.err.println("Error updating event: " + e.getMessage());
                    return false;  // 失败
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

        // 可以进一步断言成功和失败次数是否符合预期
        assertTrue(successCount > 0);  // 确保至少有一个成功
        assertTrue(failureCount > 0);  // 确保至少有一个失败（可能是由于乐观锁冲突）
    }
}