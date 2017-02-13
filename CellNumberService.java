/**
 *  Helper class to validate if a phone number is a cell phone number for
 *  US number only based on the Twilio lookup.
 *  @author    Jeffrey Lu
 *  @version   1.0
 *  @since     2016-03-25
 */
import com.twilio.sdk.LookupsClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.instance.lookups.PhoneNumber;

public class CellNumberService {

  // Find your Account Sid and Token at twilio.com/user/account
  static final String ACCOUNT_SID = "AC4955533fc1a6f9a39e86c101d7e68496"; 
  //"{{Insert account ID}}";
  static final String AUTH_TOKEN = "a0ecfad431e75d6947d520fe642aca92";
  //"{{Insert authentication token}}";

  /**
   *  Looks up and returns the type of phone number [LANDLINE, MOBILE, VOIP]
   *
   *  @param nationalNumber USA number in the format of NNNNNNNNNN
   *  @return               Type of phoneline
   *  @throws TwilioRestException
   *  @see TwilioRestException 
   */
  public static String isTwilioMobile(long nationalNumber) throws TwilioRestException {
    
    // Setup lookup client
    LookupsClient client = new LookupsClient(ACCOUNT_SID, AUTH_TOKEN);

    // Timer
    long startTime = System.nanoTime();
    long endTime;
    
    // Setup number to check the type of line
    PhoneNumber number = 
      client.getPhoneNumber(String.valueOf(nationalNumber), "US", true);

    endTime = System.nanoTime();
    
    System.out.println("Time: " + (endTime - startTime));
      
    // Return type of line
    return String.valueOf(number.getType());
  }
  
}