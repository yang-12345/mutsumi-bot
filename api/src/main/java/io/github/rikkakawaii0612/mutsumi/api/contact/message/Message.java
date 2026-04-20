package io.github.rikkakawaii0612.mutsumi.api.contact.message;

public interface Message {
    String asString();

    void visit(Visitor visitor);

    default MessageChain append(Message message) {
        return MessageChain.of(this, message);
    }

    static Text text(String text) {
        return () -> text;
    }

    static At at(long target) {
        return () -> target;
    }

    interface Visitor {
        void accept(SingleMessage message);
    }
}
