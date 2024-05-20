package com.delta.mobileplatform.web.controller.utils.link_list

class Node<T>(val value: T, var next: Node<T>? = null)

class LinkedList<T> : Iterable<T?> {
    private var head: Node<T>? = null
    private var tail: Node<T>? = null

    fun isEmpty() = head == null

    fun append(value: T) {
        val newNode = Node(value)
        if (isEmpty()) {
            head = newNode
            tail = newNode
        } else {
            tail?.next = newNode
            tail = newNode
        }
    }

    fun setNextToFirst() {
        head = tail?.next
    }

    override fun iterator(): Iterator<T?> {
        return LinkedListIterator()
    }

    private inner class LinkedListIterator : Iterator<T?> {
        private var current: Node<T>? = head

        override fun hasNext() = current != null

        override fun next(): T? {
            val value = current?.value
            current = current?.next
            return value
        }
    }
}