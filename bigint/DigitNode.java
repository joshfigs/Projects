package bigint;

public class DigitNode {
    int digit;  //This instance variable represents the digit stored in the node
    DigitNode next; //This instance variable is a reference to the next DigitNode in the linked list

    //constructor needed to make DigitNode objects with data fields
    DigitNode(int digit, DigitNode next) {
        this.digit = digit;
        this.next = next;
    }

    //Returns a string representation of the digit stored in the node
    public String toString() {
      return digit + "";
   }
}
