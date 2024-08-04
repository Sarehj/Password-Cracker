import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Iterator;



public class PasswordCrack {

    static ArrayList <String> dictWList;
    static ArrayList <String> mangleL;
    static CopyOnWriteArrayList <String> passWList;
    static Iterator<String>  iterator;
    static BufferedWriter buffwriter = null;
     
    
    public static void main (String args[]) throws IOException {

        FileInputStream in1 = null;  
        FileInputStream in2 = null;
        InputStreamReader in1Reader = null; 
        InputStreamReader in2Reader = null; 
        BufferedReader buffreader1 = null; 
        BufferedReader buffreader2 = null;
        

        if (args.length !=2){
            System.out.println("2 arguments need <dictionary_file> <password_file>");
            System.exit(1);
        }


    try {
        
          File inputFile1 = new File (args[0]);     
          in1 = new FileInputStream(inputFile1);
          in1Reader = new InputStreamReader(in1);
          buffreader1 = new BufferedReader(in1Reader);

          
        String input;
        dictWList = new ArrayList <> ();
        while ((input = buffreader1.readLine()) != null) {
        	
            dictWList.add(input);    // added words to the dictionary  
        }

        in1.close();
        in1Reader.close();
        buffreader1.close();

    } 
    catch(FileNotFoundException e){ 
      System.out.println("Access denied, file does not found or can not read!"); 
      System.exit(1);
  }
        

    try{  
        File inputFile2 = new File (args[1]);   //password file
        in2 = new FileInputStream(inputFile2);    
        in2Reader = new InputStreamReader(in2);
        buffreader2 = new BufferedReader(in2Reader);
        buffwriter = new BufferedWriter(new FileWriter("passwd2-plain.txt"));
        
        String input2;
        passWList = new CopyOnWriteArrayList <> (); 
        while ((input2 = buffreader2.readLine()) != null) {     // extract names from file

            String[] parts = input2.split(":");
            String[] names = parts[4].split(" ");
            String firstName = names[0];
            String lastName = names[1];

            passWList.add(parts[1]);      // add the hashed password 
            dictWList.add(firstName);     // add names to the dictionary
            dictWList.add(lastName);
             
      }
       
        in2.close();
        in2Reader.close();
        buffreader2.close();

    } 
    catch(FileNotFoundException e){ 
      System.out.println("Access denied, file does not found or can not read!"); 
      System.exit(1);
  }

    //add common words to dictionary
    dictWList.add("123");
    dictWList.add("1234");
    dictWList.add("12345");
    dictWList.add("123456");
    dictWList.add("1234567");
    dictWList.add("12345678");
    dictWList.add("123456789");
    dictWList.add("987654321");
    dictWList.add("0987654321");
    dictWList.add("password");
    dictWList.add("passw0rd");
    dictWList.add("letmein");
    dictWList.add("111111");
    dictWList.add("123123");
    
 
  // compare passwords
  for(String PFword : dictWList){
      compare(PFword);
  }
  
  if (passWList.isEmpty() == false) {
  
  //mangle
  Mangle(dictWList);
  }
  else {
	  buffwriter.close();
      return;
  }
 
    
}
   
    
   //Mangle
   private static void Mangle (ArrayList<String> List) throws IOException {
       
        mangleL = new ArrayList<>();

        for (String word : List) {
            //checking if the password is correct, add every mangle to mangle list 

            if (word.length() > 0) {
                
                mangleL.add(compare(reverse(word)));
                mangleL.add(compare(reflect2(word)));
                mangleL.add(compare(uppercase(word)));
                mangleL.add(compare(lowercase(word)));
                mangleL.add(compare(capitalize(word)));
                mangleL.add(compare(ncapitalize(word)));
                mangleL.add(compare(toggle1(word)));
                mangleL.add(compare(toggle2(word)));
            
         //don't remove characters with no characters    
         try {
            mangleL.add(compare(deleteFirst(word)));
            } 
        catch (Exception e) {
            }
                //Some mangles apply if word.length <= 8
                if (word.length() <= 8) {
    
                    mangleL.add(compare(deleteLast(word)));
                    mangleL.add(compare(duplicate(word)));
                    mangleL.add(compare(reflect1(word)));
               
                    for (int j = 0; j < lettersArray.length; j++) {
                    	 mangleL.add(compare(prependC(word, j))); 
                    	 mangleL.add(compare(appendC(word, j)));
                    }
                }
            }
        }


      //  Call mangle until all passwords are found 
        try {
            Mangle(mangleL);
        } catch (OutOfMemoryError e) {
            System.exit(0);
        }


   }
   
     
        public static String compare(String word) throws IOException {

            iterator = passWList.iterator();
    
            while (iterator.hasNext()) {
               
            	String password = iterator.next();  //extract hashed password
                String compareHash = jcrypt.crypt(password, word);
    
                if (passWList.contains(compareHash)) {
                   
                    buffwriter.write(word);  //write on the file 
                    buffwriter.newLine(); 
                   
                    System.out.println(word);    //print 
            
                    passWList.remove(compareHash);
                    //If all passwords found
                    if (passWList.isEmpty()) {
                        System.exit(0);
                    }
                }
                
            }
            buffwriter.flush();
            return word;
        
    
}


public static char[] lettersArray = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

//prepend a character to the string
public static String prependC(String word, int c) {
    char character = lettersArray[c];
    return character + word;
}

//append a character to the string
public static String appendC(String word, int c) {
    char character = lettersArray[c];
    return word + character;
}

public static String deleteFirst(String word) {
    return word.substring(1);
}

public static String deleteLast(String word) {
    return word.substring(0, word.length() - 1);
}

public static String reverse(String word) {
    return new StringBuilder(word).reverse().toString();
}

public static String duplicate(String word) {
    return word + word;
}

public static String reflect1(String word) {
    return word + reverse(word);
}

public static String reflect2(String word) {
    return reverse(word) + word;
}

public static String uppercase(String word) {
    return word.toUpperCase();
}

public static String lowercase(String word) {
    return word.toLowerCase();
}

public static String capitalize(String word) {
    return word.substring(0, 1).toUpperCase() + word.substring(1);
}

public static String ncapitalize(String word) {
    return word.substring(0, 1).toLowerCase() + word.substring(1).toUpperCase();
}


//we have two toggle : 
//letter in odd positions change to toUpperCase OR
//letter in even positions change to toUpperCase
public static String toggle1(String word) {
    String toggle = "";
    for (int i = 0; i < word.length(); i++) {
        if (i % 2 == 0) {  
            toggle += word.substring(i, i+1).toUpperCase();
        } else {
            toggle += word.substring(i, i+1);
        }
    }
    return toggle;
}

public static String toggle2(String word) {
    String toggle = "";
    for (int i = 0; i < word.length(); i++) {
        if (i % 2 != 0) {                
            toggle += word.substring(i, i+1).toUpperCase();
        } else {
            toggle += word.substring(i, i+1);
        }
    }
    return toggle;
}



}
