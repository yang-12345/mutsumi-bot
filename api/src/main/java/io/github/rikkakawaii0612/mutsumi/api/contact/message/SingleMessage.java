package io.github.rikkakawaii0612.mutsumi.api.contact.message;

public interface SingleMessage extends Message {
    @Override
    default void visit(Message.Visitor visitor) {
        visitor.accept(this);
    }
}
