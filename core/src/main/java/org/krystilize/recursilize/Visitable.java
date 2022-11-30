package org.krystilize.recursilize;

/**
 * An object that is visitable by {@link Visitor}
 *
 * @param <T> the type of the object being visited
 */
public interface Visitable<T> {
    /**
     * Visit the object. This method must be able to be ran multiple times with the exact same behaviour.
     *
     * @param visitor the visitor
     */
    void visit(Visitor<T> visitor);
}
