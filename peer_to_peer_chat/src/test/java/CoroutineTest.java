import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.coroutine.CoroutineType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICorutine;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_coroutine.CoroutineService;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class CoroutineTest {
    private static final Logger logger = LoggerFactory.getLogger(CoroutineTest.class);
    private static CoroutineService coroutineService;

    @BeforeAll
    public static void setUp() {
        System.out.println("Running CoroutineTest");
        coroutineService = CoroutineService.getInstance();
        coroutineService.start();
    }

    @AfterAll
    public static void tearDown() throws InterruptedException {
        coroutineService.stop();
    }

    @Test
    public void coroutineAddRunTest() throws InterruptedException {
        // Arrange
        CountDownLatch latch = new CountDownLatch(1);
        int step = 1;
        int target = 5;
        final int[] assertMe = {0};

        // Act
        coroutineService.addCoroutine(new ICorutine() {
            private int i = -1;

            @Override
            public CoroutineType getCoroutineType() {
                return CoroutineType.FINISHABLE;
            }

            @Override
            public boolean resume() {
                i += step;
                if (i >= target) {
                    latch.countDown();
                    assertMe[0] = i;
                    return false;
                }
                return true;
            }
        });

        // Wait for coroutine to finish
        latch.await();

        // Assert
        Assertions.assertEquals(target, assertMe[0]);
        System.out.printf("Test passed %d : %d%n", target, assertMe[0]);
    }

    @Test
    public void timedCoroutineAddRunTest() throws InterruptedException {
        // Arrange
        CountDownLatch latch = new CountDownLatch(1);
        int target = 1;
        final Boolean[] flag = {false};

        // Act
        coroutineService.addCoroutine(new ICorutine() {
            final Date curTime = new Date();
            final int endDeltaTimeSec = target;

            @Override
            public CoroutineType getCoroutineType() {
                return CoroutineType.FINISHABLE;
            }

            @Override
            public boolean resume() {
                if (new Date().getTime() >= curTime.getTime() + endDeltaTimeSec * 1000L) {
                    flag[0] = true;
                    latch.countDown();
                    return false;
                }
                return true;
            }
        });

        for (int i = 0; i < target + 1; ++i) {
            System.out.println("Step " + i);
            Thread.sleep(1000);
        }

        // Wait for coroutine to finish
        latch.await();

        // Assert
        Assertions.assertTrue(flag[0]);
        System.out.printf("Test passed %b%n", flag[0]);
    }
}
