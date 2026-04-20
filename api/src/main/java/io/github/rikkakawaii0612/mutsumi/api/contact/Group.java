package io.github.rikkakawaii0612.mutsumi.api.contact;

import java.util.List;

public interface Group {
    long getId();

    List<Member> getMembers();
}
