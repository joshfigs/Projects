package bigint;
public class BigInteger {
    //True if this is a negative integer
    boolean negative;
    //# of digits in this integer
    int numDigits;
    //Represents the front of the linked list storing the digits of the BigInteger
    DigitNode front;
    
    //Constructor intializes a new BigInteger obj w/ default values(not negative, zero digits, and null front)
    public BigInteger() {
        negative = false;
        numDigits = 0;
        front = null;
    }

    //Parse a string that represents a large integer
    public static BigInteger parse(String integer) 
    throws IllegalArgumentException {
        //Remove all leading or trailing spaces
        integer = integer.trim();
        //Initialize the BigInteger
		BigInteger bigInt = new BigInteger();

        //Store values to know whether to ignore a sign during parsing
		boolean negative = false, hasExplicitSign = false;
		int stringLength;
		if(integer != null) {
			stringLength = integer.length();
		} else {
			throw new IllegalArgumentException("Please input a valid number");
		}

        //If no number was inputed or only a sign with no corresponding 
		//number was inputed, throw exception
		if(stringLength == 0) {
			throw new IllegalArgumentException("Please input a valid number.");
		} else if(stringLength == 1 && integer.substring(0,1).compareTo("-") <= 0) {
			throw new IllegalArgumentException("Please input a valid number.");
		}

        //Otherwise check whether the number is correctly formatted 
		else if(stringLength > 0) {
			//Keep track of the last indexes of the signs
			//(Initialize at -1 because there might not be signs)
			int lastIndexNeg = -1;
			int lastIndexPos = -1;
			for(int i = 0; i < stringLength; i++) {
				char digitOrSign = integer.charAt(i);
				if(digitOrSign == '-')
					lastIndexNeg = i;
				else if(digitOrSign == '+' ) 
					lastIndexPos = i;
				else if(!Character.isDigit(digitOrSign))
					throw new IllegalArgumentException("Please input a valid number.");
			}
        

        //Check whether there are stray signs within the number, whether a + or - was found after 1st position
			if(lastIndexNeg > 0 || lastIndexPos > 0) {
				throw new IllegalArgumentException("Please input a valid number.");
			}

            //Determine whether a sign was explicitly given, and if so,
			//whether the sign is positive or negative.
			if(lastIndexNeg == 0) {
				hasExplicitSign = true;
				negative = true;
			} else if(lastIndexPos == 0) {
				hasExplicitSign = true;
			}

            //If the sign was explicit, we don't need to worry about it anymore for parsing
			if(hasExplicitSign) {
				integer = integer.substring(1);
				stringLength--;
			}
			
			if(stringLength == 1 && integer.charAt(0) == '0') {
				return bigInt;
			}
		}

        //Get the last digit (ones place) and initialize bigInt.front
		int lastDigit = Integer.parseInt(integer.substring(stringLength-1, stringLength));
		bigInt.front = new DigitNode(lastDigit, null);
		
		//Get the index at which to stop looking for meaningful digits
		//if there are leading zeroes
		int firstNonzeroIdx = 0;
		int i = stringLength-1; //used later for string traversal
		for(; firstNonzeroIdx < stringLength; firstNonzeroIdx++) {
			if(integer.substring(firstNonzeroIdx, firstNonzeroIdx+1).compareTo("0") > 0) {
				break;
			}
		}
        
        //There is no point in a sign if the number is 0, and return it right away
		if(firstNonzeroIdx > i || (lastDigit == 0 && stringLength == 1)) {
			bigInt.front = null;
			bigInt.negative = false;
			bigInt.numDigits = 0;
			return bigInt;
		}
		
        //Otherwise, keep the sign and length
		bigInt.negative = negative;
		bigInt.numDigits = stringLength;
		
		//Create the linked list, parsed int is inputted from LSB to MSB, no special
		for(DigitNode curr = bigInt.front; i > firstNonzeroIdx; curr = curr.next, i--) {
			//extracts a substring of length 1 from the string integer, starting from the character at index i-1 and ending at the character at index i
			String subStr = integer.substring(i-1, i);
			//means it represents a digit character (0-9)
			if(subStr.compareTo("-") > 0) {
				//converts character to an integer and adds a new node to the linked list 
				curr.next = new DigitNode(Integer
					.parseInt(integer.substring(i-1, i)),  null);
			}
			
		}
		
		return bigInt;
	}


    public BigInteger add(BigInteger other) {
		//If the other number is null or 0, that is equivalent
		//to zero. Addition will not change the value of 'this'.
		if(other == null || other.front == null) {
			return this;
		} 
		//Otherwise, if this number is 0 while the other number is
		//not null, just return other since it will remain unaltered
		else if(this.front == null) {
			return other;
		}
		
		//Initialize our BigInteger for the answer
		BigInteger answer = new BigInteger();
		
		//If both numbers are negative or both are positive,
		//Just ADD them up and carry the sign
		if(this.negative == other.negative) {
			answer.negative = this.negative;
			answer.front = new DigitNode(0, null);
			
			//Initialize pointers to all the integers being added and the answer
			DigitNode thisPtr = this.front, otherPtr = other.front, ansPtr = answer.front;
			int carry = 0;
			
			//While there are digits that can be added, sum them up
			//and set the current digit of answer to the sum
			for(; thisPtr != null && otherPtr != null; 
					thisPtr = thisPtr.next, otherPtr = otherPtr.next) {
				
				int digitSum = thisPtr.digit + otherPtr.digit + carry;
				carry = digitSum / 10; //If digitSum > 9, carry the tens places
				//stores current digit
				ansPtr.digit = digitSum % 10;
				
				//Only create the next node in the answer if there
				//are more digits to be added/tacked on
				if(thisPtr.next != null || otherPtr.next != null) {
					//making sure a new node is added incase of carryover
					ansPtr.next = new DigitNode(0,null);
					ansPtr = ansPtr.next;
				}
			}
			
			//Only one of the following while loops will run if 
			//one of the numbers being added was greater than the other,
			//essentially tacking on the remaining digits to answer.
			for(; thisPtr != null ; thisPtr = thisPtr.next) {
				int digitSum = thisPtr.digit + carry;
				carry = digitSum / 10;
				ansPtr.digit = digitSum % 10;
				if(thisPtr.next != null) {
					ansPtr.next = new DigitNode(0,null);
					ansPtr = ansPtr.next;
				}
			}
			
			for(; otherPtr != null; otherPtr = otherPtr.next) {
				int digitSum = otherPtr.digit + carry;
				carry = digitSum / 10;
				ansPtr.digit = digitSum % 10;
				if(otherPtr.next != null) {
					ansPtr.next = new DigitNode(0,null);
					ansPtr = ansPtr.next;
				}
			}
			
			//Make sure the carry bit is fully carried through
			if(carry != 0) {
				ansPtr.next = new DigitNode(carry, null);
			}
		}
		
		//Otherwise exactly one of the integers is negative,
		//in which case we have to SUBTRACT
		else {
			//Keep track of which number is greater and lesser
			BigInteger greater, lesser;
			
			//Copy the respective BigIntegers into their variables
			if(isGreater(other)) {
				greater = other;
				lesser = this;
			} else {
				greater = this;
				lesser = other;
			}
			//answer's negative would generally match whichever had greater magnitude
			answer.negative = greater.negative;
			
			//Initialize the answer to be returned
			//The sign of answer corresponds to the integer of greater magnitude
			answer.front = new DigitNode(0, null);
			
			//Initialize pointers to all the integers being added and the answer
			DigitNode gtrPtr = greater.front, lsPtr = lesser.front, ansPtr = answer.front;
			
			//While there are digits to subtract, do so while keeping
			//track of borrowing
			boolean hasBorrowed = false, borrowed = false;
			for(; gtrPtr != null && lsPtr != null; gtrPtr = gtrPtr.next, lsPtr = lsPtr.next, ansPtr = ansPtr.next) {
				
				//If the digit of the number being subtracted from is less
				//than the number being subtracted from it, we need to
				//borrow from the next digit then subtract
				int gtrDigit = gtrPtr.digit;
				if(borrowed) //If we previously borrowed, decrement the digit
					gtrDigit--;
				
				//If the digit cannot be subtracted from, borrow and keep track of that
				if(gtrDigit < lsPtr.digit || gtrDigit < 0) {
					gtrDigit += 10;
					hasBorrowed = true;
				}
				
				//Assign the difference and check if it's a nonzero number
				int diff = gtrDigit - lsPtr.digit;
				ansPtr.digit = diff;
				
				//Only create the next node in the answer if there
				//are more digits to be added/tacked on
				if(gtrPtr.next != null || lsPtr.next != null) {
					ansPtr.next = new DigitNode(0,null);
				}
				
				//If we borrowed for the current digit, we'll have to decrement the next digit
				borrowed = hasBorrowed;
				//set to false because if we borrowed, we're done borrowing for this digit
				hasBorrowed = false;
			}
			
			
			//Copy any remaining digits in the greater number
			//(lesser cannot have more digits than greater so
			// we do not need to worry about it).
			for(; gtrPtr != null; gtrPtr = gtrPtr.next) {
				//Transfer the digit and check whether it's a nonzero number
				int gtrDigit = gtrPtr.digit;
				if(borrowed)
					gtrDigit--;
				
				if(gtrDigit < 0) {
					gtrDigit += 10;
					hasBorrowed = true;
				}
				ansPtr.digit = gtrDigit;
				
				//Only make a new node if there are more digits to copy
				if(gtrPtr.next != null) {
					ansPtr.next = new DigitNode(0,null);
					ansPtr = ansPtr.next;
				}
				
				borrowed = hasBorrowed;
				hasBorrowed = false;
			}	
		}
		
		//Keep track of the last non-zero digit for truncating leading zeros
		int count = 0, lastNonzero = 0;
		answer.numDigits = 0;
		for(DigitNode ptr = answer.front; ptr != null; ptr = ptr.next, count++) {
			if(ptr.digit != 0) {
				lastNonzero = count;
			}
		}
		
		//Loop through to increment numDigits correctly and truncate leading zeros
		count = 0;
		for(DigitNode ptr = answer.front; ptr != null; ptr = ptr.next, count++) {
			answer.numDigits++;
			if(count == lastNonzero) {
				ptr.next = null;
				break;
			}
		}
		
		//If the number is zero, adjust the variables to represent 0 
		if(answer.numDigits == 1 && answer.front.digit == 0
			|| answer.numDigits == 0) {
			answer.negative = false;
			answer.front = null;
			answer.numDigits = 0;
		}

		return answer;
	}
	
	/**
	 * Checks whether the BigInteger inputed as the
	 * parameter is greater than this integer in terms of
	 * magnitude (ignoring sign) 
	 * @param other Another BigInteger
	 * @return checks whether {@code other} is greater than {@code this}
	 */
	// this method provides a way to compare the magnitude of two BigIntegers regardless of their signs,
	//allowing for proper comparison in arithmetic operations like addition and subtraction.
	private boolean isGreater(BigInteger other) {
		if(this.numDigits > other.numDigits) {
			return false;
		} else if(other.numDigits > this.numDigits) {
			return true;
		} else {
			DigitNode ptr1 = this.front, ptr2 = other.front;
			boolean greater = false;
			while(ptr1 != null && ptr2 != null) {
				if(ptr2.digit > ptr1.digit) {
					greater = true;
				} else if(ptr2.digit < ptr1.digit) {
					greater = false;
				}
				ptr1 = ptr1.next;
				ptr2 = ptr2.next;
			}
			return greater;
		}
	}
	
	/**
	 * Returns the BigInteger obtained by multiplying the given BigInteger
	 * with this BigInteger - DOES NOT MODIFY this BigInteger
	 * 
	 * @param other BigInteger to be multiplied
	 * @return A new BigInteger which is the product of this BigInteger and other.
	 */
	public BigInteger multiply(BigInteger other) {
		//Initialize the BigInteger to store the answer;
		BigInteger answer = new BigInteger();
		
		//If either number is 0, the result will be 0;
		if(other == null || other.front == null || this.front == null) {
			return answer;
		}
		
		//Initialize front
		answer.front = new DigitNode(0, null);
		
		//Keep track of how many zeros to add in front of the products to be added
		int numZeros = 0;
		//For each digit in the other BigInteger, multiply
		//it by every digit in this BigInteger
		for(DigitNode otherPtr = other.front; otherPtr != null; otherPtr = otherPtr.next, numZeros++) {
			
			//Initialize the scalar multiple, its front, and its number of digits
			//(If we are inside this loop, the number of digits has to equal at least 1)
			BigInteger scalarMultiple = new BigInteger();
			scalarMultiple.front = new DigitNode(0, null);
			DigitNode scalarPtr = scalarMultiple.front;
			
			//Add the necessary zeros at the beginning of the multiple
			//based on the digit in 'other' that we are multiplying by
			for(int j = 0; j < numZeros; j++) {
				scalarPtr.digit = 0;
				scalarPtr.next = new DigitNode(0,null);
				scalarPtr = scalarPtr.next;
			}
			
			//For every digit in this integer, multiply it
			//by each digit in other and add the carry bits
			int carry = 0;
			for(DigitNode thisPtr = this.front; 
					thisPtr != null; 
					thisPtr = thisPtr.next) {
				
				//The carry needs to be added onto the product
				int product = thisPtr.digit * otherPtr.digit + carry;
				
				//tens place
				carry = product / 10;
				//1's place
				scalarPtr.digit = product % 10;
				
				//Only create the next node if we are not at the
				//end of the integer
				if(thisPtr.next != null) {
					scalarPtr.next = new DigitNode(0,null);
					scalarPtr = scalarPtr.next;
				} else {
					//Carry through the last carry bit if necessary
					if(carry != 0)
						scalarPtr.next = new DigitNode(carry,null);
					break;
				}
			}
			
			//Add to answer the scalar multiple that we just got
			answer = answer.add(scalarMultiple);
		}
		
		//Set the negative value of the answer based on the scenario
		//(sign is + if +*+ or -*-, and is - if +*-)
		//This is done after the multiplication since add might override the sign
		if(this.negative == other.negative) {
			answer.negative = false;
		} else {
			answer.negative = true;
		}
		
		//Keep track of the last non-zero digit for truncating leading zeros
		int count = 0, lastNonzero = 0;
		answer.numDigits = 0;
		for(DigitNode ptr = answer.front; ptr != null; ptr = ptr.next, count++) {
			if(ptr.digit != 0) {
				lastNonzero = count;
			}
		}
		
		//Loop through to increment numDigits correctly and truncate leading zeros
		count = 0;
		for(DigitNode ptr = answer.front; ptr != null; ptr = ptr.next, count++) {
			answer.numDigits++;
			if(count == lastNonzero) {
				ptr.next = null;
				break;
			}
		}
		
		//If the number is zero, adjust the variables to represent 0 
		if(answer.numDigits == 1 && answer.front.digit == 0
			|| answer.numDigits == 0) {
			answer.negative = false;
			answer.front = null;
			answer.numDigits = 0;
		}

		return answer;
	}
	
	//to String for 0 values
	public String toString() {
		if (front == null) {
			return "0";
		}
		
		//Concatenating the digit with an empty string, which implicitly converts the digit to a string.
		//This is the starting point for building the string representation.
		String retval = front.digit + "";
		//Iterates through remaining digits of BigInteger, starting from the digit after the front node
		//continues until there are no more digits
		for (DigitNode curr = front.next; curr != null; curr = curr.next) {
				retval = curr.digit + retval;
		}
		
		//Give - values the appropriate sign 
		if (negative) {
			retval = '-' + retval;
		}
		
		return retval;
	}
}
	

    