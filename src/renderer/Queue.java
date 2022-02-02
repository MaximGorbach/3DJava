package renderer;

public class Queue<E>{
    QueueElement<E> front;
    QueueElement<E> end;
    int length;

    public Queue(){
        front = null;
        end = null;
        length = 0;
    }

    public void enqueue(E val){
        QueueElement<E> newElem = new QueueElement<>(val);
        if(front == null){
            front = newElem;
            end = newElem;
            length = 1;
        } else {
            end.next = newElem;
            end = newElem;
            length++;
        }
    }

    public E peek(){
        return front.val;
    }

    public E dequeue(){
        if(front == null){ return null;}
        E val = front.val;
        front = front.next;
        length--;
        return val;
    }

    public boolean isEmpty(){
        return(length == 0);
    }

    public String toString(){
        String res = "[";
        for(int i = 0; i < length-1; i++){
            E elem = this.dequeue();
            res += elem.toString() + ", ";
            this.enqueue(elem);
        }
        E elem = this.dequeue();
        if(elem != null){ res += elem.toString();}
        res += "]";
        this.enqueue(elem);
        return res;
    }
}

class QueueElement<E>{
    E val;
    QueueElement<E> next;

    public QueueElement(E element){
        this.val = element;
        this.next = null;
    }
}