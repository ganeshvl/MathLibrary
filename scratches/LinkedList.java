public class LinkedList {

    // private ListNode head;


    public void display(ListNode head) {
        if (head == null) {
            return;
        }

        ListNode current = head;

        while (current != null) {
            System.out.println("Data-->" + current.data);
            current = current.next;
        }
        System.out.println(current);
    }

    public ListNode insertNodeatBegining(ListNode head, int data) {

        ListNode newNode = new ListNode(data);

        if (head == null) {
            return newNode;
        }

        newNode.next = head;
        head = newNode;

        return head;

    }

    public void lengthOFLL(ListNode head) {

        if (head == null) {
            return;
        }

        ListNode current = head;

        int sizeCounter = 0;

        while (current != null) {
            sizeCounter++;
            current = current.next;
        }

        System.out.println("Length of the Linked List-->" + sizeCounter);

    }


    private static class ListNode {
        int data;
        ListNode next;

        ListNode(int data) {
            this.next = null;
            this.data = data;
        }

    }

    public static void main(String[] args) {
        ListNode head = new ListNode(10);
        ListNode second = new ListNode(11);
        ListNode third = new ListNode(12);
        ListNode fourth = new ListNode(13);
        head.next = second;
        second.next = third;
        third.next = fourth;
        fourth.next = null;


        LinkedList list = new LinkedList();
        list.display(head);
        list.lengthOFLL(head);

        ListNode newNode = list.insertNodeatBegining(head, 15);
        list.display(newNode);
        list.lengthOFLL(newNode);


    }
}