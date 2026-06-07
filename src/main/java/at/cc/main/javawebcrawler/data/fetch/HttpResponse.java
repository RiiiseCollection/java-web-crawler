package at.cc.main.javawebcrawler.data.fetch;

public record HttpResponse(int statusCode, String body, String url) {
}
