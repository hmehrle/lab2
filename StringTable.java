import java.util.Arrays;


//
// STRINGTABLE.JAVA
// A hash table mapping Strings to their positions in the the pattern sequence
// You get to fill in the methods for this part.
//
public class StringTable {
	Record[] hashTable;  
	int counter; 
	//
	// Create an empty table big enough to hold maxSize records.
	//
	public StringTable(int maxSize) 
	{
		this.hashTable = new Record[2]; 
		counter=0;
	}

	//
	// Insert a Record r into the table.  Return true if
	// successful, false if the table is full.  You shouldn't ever
	// get two insertions with the same key value, but you may
	// simply return false if this happens.
	//
	public boolean insert(Record r) 
	{ 
		if (counter > (hashTable.length/4)){ //this is the load factor of 1/4
			doubler(r); //call the doubling method
		return true; 
		}

		int BaseKey = baseHash(toHashKey(r.key)); //convert the base key to an int
		int StepKey = stepHash(toHashKey(r.key)); //convert step key to an int
		int BaseMod = BaseKey % this.hashTable.length; // make sure the hash is always within table size
		if (this.hashTable[BaseMod] != null) { //if the spot isnt null
			if (this.hashTable[BaseMod].equals(r)) { //and the record at the spot = your record
				return false;
			}
		}

			if (this.hashTable[BaseMod] == null) { // if the first spot is null

				r.recordHash = toHashKey(r.key); //convert to an int
				this.hashTable[BaseMod] = r; // enter the key
				counter ++; 
				return true; 
			}
			else { 

				for (int i = 1; i < this.hashTable.length; i++) { //iterate through the table
					int modVal = (BaseKey*(i-1) + StepKey) % this.hashTable.length; 
					if (this.hashTable[modVal] == null) { // if its = null ever
						r.recordHash = toHashKey(r.key);
						
						this.hashTable[modVal] = r; // enter the key
						counter ++; 
						return true;
					}	 
				}
			}

		return false;  
	}
	//
	// Delete a Record r from the table.  Note that you'll have to
	// find the record first unless you keep some extra information
	// in the Record structure.
	//
	public void remove(Record r) 
	{
		Record deleted = new Record("deleted");
		int BaseKey = baseHash(toHashKey(r.key));
		int StepKey = stepHash(toHashKey(r.key));
		int BashHash = BaseKey % this.hashTable.length;
		if (this.hashTable[BaseKey] != null) { //if it isnt =null
			if (this.hashTable[BashHash] == r) {// and its what youre looking for 
				counter--;
				this.hashTable[BashHash] = deleted; // delete it
			}
		}
		for (int i = 1; i < this.hashTable.length; i++) {//iterate thorugh 
			int StepHash = (BaseKey*(i-1) + StepKey) % this.hashTable.length; 
			if (this.hashTable[StepHash] == null) { //if it equals null do nothing
				return; 
			}
			else {
				if (this.hashTable[StepHash] == r) { //if the spot ever matches the record you want to delete
					counter--;
					this.hashTable[StepHash] = deleted;// delete it 
				}
			}
		}
		return; 
	}



	//
	// Find a record with a key matching the input.  Return the
	// record if it exists, or null if no matching record is found.
	//
	public Record find(String key) 
	{
		Record deleted = new Record("deleted");
		int BaseKey = baseHash(toHashKey(key));
		int StepKey = stepHash(toHashKey(key));
		int BashHash = BaseKey % this.hashTable.length; 
		if (this.hashTable[BashHash] != null) {  // if its not null 
			if (this.hashTable[BashHash].recordHash == toHashKey(key)) {// and its the right one 
				if (this.hashTable[BashHash].key.equals(key)) {
					return this.hashTable[BashHash];  // return the key
				}
			}  
		}
		for (int i = 1; i < this.hashTable.length; i++) { // iterate through
			int StepHash = (BaseKey*(i-1) + StepKey) % this.hashTable.length; 

			if (this.hashTable[StepHash] == null) { // if its null return null
				return null;
			}
			if (this.hashTable[StepHash].key == "deleted") { 
				return deleted;
			}
			else {
				if (this.hashTable[StepHash].recordHash == toHashKey(key)) { // other wise 
					if (this.hashTable[StepHash].key.equals(key)) {
						return this.hashTable[StepHash]; // return the key
					}
				}
			}
		}
		return null; 
	}

	  public void doubler(Record r) {
		Record[] oldTable = Arrays.copyOf(hashTable, hashTable.length); // make a copy
		int doubleAmount = 2; 
		hashTable = new Record[hashTable.length*doubleAmount];// double the size of the table
		counter = 0; 
		for (int i = 1; i < hashTable.length; i++) {
			if (oldTable[i] != null || oldTable[i].key != "deleted") { // if its not null 
				insert(oldTable[i] ); // insert it into the table
			}
		}
		insert(r); // insert the new record
	} 


	///////////////////////////////////////////////////////////////////////


	// Convert a String key into an integer that serves as input to hash
	// functions.  This mapping is based on the idea of a linear-congruential
	// pesudorandom number generator, in which successive values r_i are 
	// generated by computing
	//    r_i = ( A * r_(i-1) + B ) mod M
	// A is a large prime number, while B is a small increment thrown in
	// so that we don't just compute successive powers of A mod M.
	//
	// We modify the above generator by perturbing each r_i, adding in
	// the ith character of the string and its offset, to alter the
	// pseudorandom sequence.
	//
	int toHashKey(String s)
	{
		int A = 1952786893;
		int B = 367257;
		int v = B;

		for (int j = 0; j < s.length(); j++)
		{
			char c = s.charAt(j);
			v = A * (v + (int) c + j) + B;
		}

		if (v < 0) v = -v;
		return v;
	}

	int baseHash(int hashKey)
	{
		double A = .5*(Math.sqrt(5)-1);
		double s = A * hashKey; 
		double fraction = (s) - (int)(s) ; 
		int baseHashCode = (int) (this.hashTable.length * fraction);
		return baseHashCode;
	}

	int stepHash(int hashKey)
	{
		double A = (Math.sqrt(3)-1);
		double s = A * hashKey; 
		double fraction = (s) - (int)(s) ; 
		int stepHashCode = (int) (this.hashTable.length * fraction);
		return stepHashCode;
	}
}
