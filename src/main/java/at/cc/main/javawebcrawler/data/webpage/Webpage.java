package at.cc.main.javawebcrawler.data.webpage;

import java.util.List;
import java.util.Set;

public record Webpage(Link root, Set<Link> links, List<Headline> headlines, int depth) {}
