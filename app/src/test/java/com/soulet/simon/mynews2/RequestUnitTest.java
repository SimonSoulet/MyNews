package com.soulet.simon.mynews2;

import com.soulet.simon.mynews2.models.Articles;
import com.soulet.simon.mynews2.models.Multimedium;
import com.soulet.simon.mynews2.models.Result;
import com.soulet.simon.mynews2.utils.requests.NYTService;
import com.soulet.simon.mynews2.utils.requests.NYTStreams;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;

import static io.reactivex.Observable.just;
import static org.mockito.Mockito.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RequestUnitTest {

    @Rule public MockitoRule rule = MockitoJUnit.rule();
    //@Rule public ImmediateSchedulerRule immediateSchedulerRule = new ImmediateSchedulerRule();
    @Mock NYTService nytService;
    @InjectMocks NYTStreams nytStreams;

    @Test
    public void simpletest() throws Exception {
        nytService.getTopStoriesArticles();
    }

    @Test
    public void subscribeTest() throws Exception {
       when(nytService.getTopStoriesArticles()).thenReturn(
               just(new Articles(
                       Arrays.asList(
                               new Result(
                                       Arrays.asList(
                                                new Multimedium("https://image1.com")),"Test 1", "https://test1.com"),
                               new Result(
                                       Arrays.asList(
                                                new Multimedium("https://image2.com")),"Test 2", "https://test2.com")
                       )
                       )
               )
       );
        TestObserver<Articles> testObserver = nytService.getTopStoriesArticles().test();

        testObserver.awaitTerminalEvent();

        testObserver
                .assertComplete()
                .assertValueCount(1)
                .assertNoErrors()
                .assertSubscribed();

    }

    @Test
    public void getDataTest() throws Exception {
        Articles articles = new Articles(
                Arrays.asList(
                        new Result(
                                Arrays.asList(
                                        new Multimedium("https://image1.com")),"Test 1", "https://test1.com"),
                        new Result(
                                Arrays.asList(
                                        new Multimedium("https://image2.com")),"Test 2", "https://test2.com")
                )
        );
        // Preparation: mock nytService
        NYTService nytService = Mockito.mock(NYTService.class);
        Mockito.doReturn(Observable.just(articles)).when(nytService).getTopStoriesArticles();

        // Trigger
        TestObserver<Articles> testObserver = nytService.getTopStoriesArticles().test();

        // Validation
        testObserver.assertSubscribed();
        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValues(articles);
        testObserver.assertValue(l -> l.getResults().size() == 2);
        testObserver.assertValue(l -> l.getResults().get(0).getTitle().equals("Test 1"));
        testObserver.assertValue(l -> l.getResults().get(0).getUrl().equals("https://test1.com"));
        testObserver.assertValue(l -> l.getResults().get(0).getMultimedia().size() == 1);
        testObserver.assertValue(l -> l.getResults().get(0).getMultimedia().get(0).getUrl().equals("https://image1.com"));
        // clean up
        testObserver.dispose();

        // Trigger and validation
        nytService.getTopStoriesArticles()
                .test()
                .assertValues(articles)
                .assertValue(l -> l.getResults().size() == 2)
                .assertValue(l -> l.getResults().get(0).getTitle().equals("Test 1"))
                .assertValue(l -> l.getResults().get(0).getUrl().equals("https://test1.com"))
                .assertValue(l -> l.getResults().get(0).getMultimedia().size() == 1)
                .assertValue(l -> l.getResults().get(0).getMultimedia().get(0).getUrl().equals("https://image1.com"))
                .dispose();
    }

    @Test
    public void subscriptionSchedulerTest() {
        Articles articles = new Articles(
                Arrays.asList(
                        new Result(
                                Arrays.asList(
                                        new Multimedium("https://image1.com")),"Test 1", "https://test1.com"),
                        new Result(
                                Arrays.asList(
                                        new Multimedium("https://image2.com")),"Test 2", "https://test2.com")
                )
        );
        // Preparation: mock nytService
        NYTService nytService = Mockito.mock(NYTService.class);
        TestScheduler testScheduler = new TestScheduler();
        Mockito.doReturn(Observable.just(articles))
                .when(nytService)
                .getTopStoriesArticles();

        TestObserver<Articles> testObserver = nytService.getTopStoriesArticles()
                .subscribeOn(testScheduler)
                .observeOn(testScheduler)
                .test();

        testObserver.assertNotTerminated()
                    .assertNoErrors()
                    .assertValueCount(0);// "time" hasn't started so no value expected

        testScheduler.advanceTimeBy(5, TimeUnit.SECONDS);
        testObserver.assertNoErrors()
                    .assertValueCount(1)
                    .assertValues(articles);

        testScheduler.advanceTimeBy(20, TimeUnit.SECONDS);
        testObserver.assertNoErrors()
                .assertValueCount(1)
                .assertValues(articles);

        // Clean up resources
        testObserver.dispose();
    }

    @Test
    public void timeOutTest() {
        Articles articles = new Articles(
                Arrays.asList(
                        new Result(
                                Arrays.asList(
                                        new Multimedium("https://image1.com")),"Test 1", "https://test1.com"),
                        new Result(
                                Arrays.asList(
                                        new Multimedium("https://image2.com")),"Test 2", "https://test2.com")
                )
        );
        // Preparation: mock nytService
        NYTService nytService = Mockito.mock(NYTService.class);
        Mockito.doReturn(Observable.just(articles))
                .when(nytService)
                .getTopStoriesArticles()
                .delay(20, TimeUnit.SECONDS);

        TestObserver<Articles> testObserver = nytService.getTopStoriesArticles().test();
        testObserver.assertError(TimeoutException.class);

        // Clean up resources
        testObserver.dispose();
    }

}