/**
 *  This program checks to see if particular US phone numbers are valid
 *  cellphone numbers.
 *
 *  @author    Jeffrey Lu
 *  @version   1.0
 *  @since     2016-03-25
 */
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.twilio.sdk.TwilioRestException;
import java.io.*;
import java.lang.Iterable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.Set;

public class PhoneNumberService {
  
  // static members
  // Utility to handle processing phone numbers
  final static PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
  
  // Regex expressions to process North American Number Plan numbers
  final static Pattern nxx = 
    Pattern.compile("^[2-9]{1}[0-9]{2}[1]{1}[0-9]{2}[0-9]{4}$");
  final static Pattern n11 =
    Pattern.compile("^[2-9]{1}[0-9]{2}[2-9]{1}[1]{2}[0-9]{4}$");
  final static Pattern fff =
    Pattern.compile("^[2-9]{1}[0-9]{2}[5]{3}[0-9]{4}$");
  
  /**
   *  Method takes an input set of US phone numbers and returns a collection
   *  of all valid cellphone numbers
   *
   *  @param phoneNumbers       Phone numbers to check 
   *  @return Set<PhoneNumber>  Returns set of valid cellphone numbers
   *  @throws TwilioRestException
   *  @see TwilioRestException 
   */
  public static Set<PhoneNumber> getValidPhoneNumber(
    final Collection<String> phoneNumbers) throws TwilioRestException {
    
    // Create collection for storing validated phone numbers
    HashSet<PhoneNumber> validPhoneNumbers = new HashSet<PhoneNumber>();
    PhoneNumber number;
    Iterator itr = phoneNumbers.iterator();
    
    // Check each phone number string to see if it is valid and then
    // check to see if it is a cellphone number
    while(itr.hasNext()) {
      if((number = isValidPhoneNumber(itr.next().toString())) != null) {
        if(isValidCellNumber(number)) {
          validPhoneNumbers.add(number);
        }
      }
    }
  
    return validPhoneNumbers;
  }
    
  /**
   *  Method to validate if a phone number is a valid US phone number
   *
   *  @param number        Phone number to check 
   *  @return PhoneNumber  Returns the phone number if it's valid.
   *                       Otherwise, it returns null.
   */
  public static PhoneNumber isValidPhoneNumber(String number) {
    
    // National number to process if it is valid for North American
    // Numbering Plan (NANP)
    String nationalNumber;
    
    // Attempt to process the String argument into a phone number
    try {
      PhoneNumber phoneNumber = phoneUtil.parse(number, "US");
      nationalNumber = String.valueOf(phoneNumber.getNationalNumber());
      
      // Check to see if phone number follows NANP
      if (nxx.matcher(nationalNumber).matches() ||
          n11.matcher(nationalNumber).matches() ||
          fff.matcher(nationalNumber).matches()) {
        return null;
      }

      // Check to see if phone number is valid
      if(phoneUtil.isValidNumber(phoneNumber)) {
        return phoneNumber;
      }
      return null;
    }
    // Catches exception when argument does not have correct form
    catch (NumberParseException e) {
      return null;
    }
  }
  
  /**
   *  Method to validate if a phone number is a cellphone number
   *
   *  @param phoneNumbers       Phone number to check 
   *  @return boolean           Returns true if number is a cell number.
   *                            Otherwise, returns false.
   *  @throws TwilioRestException
   *  @see TwilioRestException 
   */
  public static boolean isValidCellNumber(PhoneNumber phoneNumber)
    throws TwilioRestException {
    
    // Obtain the national number from PhoneNumber
    long nationalNumber = phoneNumber.getNationalNumber();
    
    // Checks to see if the number is MOBILE
    if (CellNumberService.isTwilioMobile(nationalNumber) == "MOBILE") {    
      return true;
    }
    
    return false;
  }
  
  /**
   *  Main method to invoke the program
   *
   *  @param args[0]    Phone number to verify if it's a valid cell number or
   *                    input file to check a list of phone numbers
   *  @param args[1]    Output file for list of valid cellphone numbers if
   *                    an input file was specified
   *  @return
   *  @throws IOException
   *  @throws TwilioRestException
   *  @see IOException
   *  @see TwilioRestException 
   */
  public static void main (String args[]) 
    throws IOException, TwilioRestException {
    
    // Declare reader and writer for files
    BufferedReader reader = null;
    BufferedWriter writer = null;
    
    // Collection to hold all input phone numbers
    Collection<String> phoneNumber = new HashSet<String>();
    Set<PhoneNumber> validatedNumbers;
    
    // Temporary variables
    String temp;
    Iterator itr;
    int argsLength = args.length;
    long startTime = 0;
    long endTime = 0;
    
    // Usage example if incorrect arguments are passed in
    if (argsLength == 0 || argsLength > 2) {
      System.out.println("Usage: java PhoneNumberService phone_number");
      System.out.println("            (to validate phone_number)");
      System.out.println("   or  java PhoneNumberService inputFile outputFile");
      System.out.println("            (to validate list of phone numbers)");
      System.exit(0);
    }
    
    if (argsLength == 1) {
      // Add number to collection
      phoneNumber.add(args[0]);
      
      // Validate phone number
      validatedNumbers = getValidPhoneNumber(phoneNumber);
      
      // Print if number is valid or invalid
      if (!validatedNumbers.isEmpty()) {
        System.out.println("Valid cellphone number");
        System.out.println(validatedNumbers.toString());
      }
      else {
        System.out.println("Invalid cellphone number");
      }
    }
    
    // Use of input file with phone numbers
    if (argsLength == 2) {
      // Attempt to read from file
      try {
        reader = new BufferedReader(new FileReader(args[0]));
        writer = new BufferedWriter(new FileWriter(args[1]));
      
        // Read each line from file and store into phone number collection
        while ((temp = reader.readLine()) != null) {
          phoneNumber.add(temp);
        }
      
        // Validate if phone numbers are valid cellphone numbers
        startTime = System.currentTimeMillis();
        validatedNumbers = getValidPhoneNumber(phoneNumber);
        endTime = System.currentTimeMillis();
      
        // Write validated numbers to file
        itr = validatedNumbers.iterator();
        while(itr.hasNext()) {
          writer.write(itr.next().toString());
          writer.newLine();
        }
      }
      finally {
        // Close reader and writer
        reader.close();
        writer.close();
        System.out.println("Cell number validation took " +
          (endTime - startTime) + " milliseconds.");
        System.out.println("Done!");
      }
    }
  }
  
}