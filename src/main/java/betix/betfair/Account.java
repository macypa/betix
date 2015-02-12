package betix.betfair;

import betix.core.logger.Logger;
import betix.core.logger.LoggerFactory;
import com.google.common.util.concurrent.ListenableFuture;
import com.nurkiewicz.asyncretry.AsyncRetryExecutor;
import com.nurkiewicz.asyncretry.RetryContext;
import com.nurkiewicz.asyncretry.RetryExecutor;
import com.nurkiewicz.asyncretry.function.RetryRunnable;
import org.ho.yaml.Yaml;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

class Account {
    private static final Logger logger = LoggerFactory.getLogger(Account.class);
    private static final File COOKIES_FILE = new File("cookies.yml");

    private Map<String, String> cookies;
    private String loginUrl = "https://identitysso.betfair.com/view/login";
    private String loginFormId = "loginForm";
    private String loggedExpectedTitle = "Login";
    private String isLoggedInClass = "isLoggedIn";
    private String user = "betUserfair";
    private String pass = "z1x2c3v4b5n6";

    public static void main(String[] args) {
        Account account = new Account();
        Document doc;
        try {
            doc = account.loadPage("https://www.betfair.com/sport/football?id=57&selectedTabType=COMPETITION");

            logger.debug("cookies = {}", account.getCookies());

            for (Element event : doc.getElementsByClass("event-list")) {
                logger.debug("document.body() = {}", event.text());
            }
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    Document loadPage(String uri) throws LoginException {
        if (getCookies() == null) {
            login();
        }

        Connection connection = Jsoup.connect(uri);
        for (Map.Entry cookie : getCookies().entrySet()) {
            connection.cookie(cookie.getKey() + "", cookie.getValue() + "");
        }

        try {
            Response response = connection.execute();
            Document doc = response.parse();
            validatePage(doc);

            return doc;
        } catch (IOException e) {
            logger.error("can't load page", e);
            throw new RuntimeException("can't load page" + e.getMessage());
        }
    }

    private void validatePage(Document doc) {
        Elements isLoggedInTags = doc.getElementsByClass(isLoggedInClass);
        if (isLoggedInTags == null || isLoggedInTags.isEmpty()) {
            logger.warn("not logged in");
            throw new RuntimeException("not logged in");
        }
    }

    private void login() throws LoginException {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        RetryExecutor executor = new AsyncRetryExecutor(scheduler).
                retryOn(LoginException.class).
                retryOn(IOException.class).
                withExponentialBackoff(500, 2).     //500ms times 2 after each retry
                withMaxDelay(10_000).               //10 seconds
                withUniformJitter().                //add between +/- 100 ms randomly
                withMaxRetries(2);

        ListenableFuture<Void> future = executor.doWithRetry(new RetryRunnable() {
            @Override
            public void run(RetryContext context) throws Exception {

                Document loginPage = Jsoup.connect(loginUrl).execute().parse();
                Element loginForm = loginPage.getElementById(loginFormId);
                Elements inputs = loginForm.getElementsByTag("input");

                Connection connection = Jsoup.connect("https://identitysso.betfair.com/api/login")
                        .data("username", user)
                        .data("password", pass)
                        .method(Method.POST);

//                logger.debug("inputs = " + inputs);
//                for (Element input : inputs) {
//                    if (input.attr("value") != null && !input.attr("value").isEmpty()) {
//                        connection.data(input.attr("name"), input.attr("value"));
//                    }
//                }

                logger.debug("inputs = " + inputs);
                inputs.stream().filter(input -> input.attr("value") != null
                        && !input.attr("value").isEmpty())
                        .forEach(input -> connection.data(input.attr("name"), input.attr("value")));

                Response response = connection.execute();
                Document doc = response.parse();
                if (doc.title() == null || !doc.title().equals(loggedExpectedTitle)) {
                    logger.warn("not logged in.. unexpected Title");
                    throw new LoginException("Login failed");
                }

                setCookies(response.cookies());
            }
        });

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new LoginException("Login failed");
        }
    }

    Map<String, String> getCookies() {
        if (cookies == null) {
            try {
                cookies = Yaml.loadType(COOKIES_FILE, LinkedHashMap.class);
            } catch (Exception e) {
                logger.error("can't load cookies", e);
                return null;
            }
        }
        return cookies;
    }

    void setCookies(Map<String, String> cookies) {

        this.cookies = cookies;

        try {
            Yaml.dump(cookies, COOKIES_FILE);
        } catch (FileNotFoundException e) {
            logger.warn("Error saving cookies", e);
        }
    }
}
