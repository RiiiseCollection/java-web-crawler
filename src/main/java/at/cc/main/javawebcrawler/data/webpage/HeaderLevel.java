package at.cc.main.javawebcrawler.data.webpage;

import org.jsoup.parser.Tag;

import java.util.Optional;

public enum HeaderLevel {
    H1(1), H2(2), H3(3), H4(4), H5(5), H6(6);

    private final int level;

    HeaderLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static Optional<HeaderLevel> tagToLevel(Tag tag) {
        if (tag == null) throw new IllegalArgumentException("Tag must not be null!");

        for (HeaderLevel header : values()) {
            if (header.name().equalsIgnoreCase(tag.name())) {
                return Optional.of(header);
            }
        }
        return Optional.empty();
    }


}
