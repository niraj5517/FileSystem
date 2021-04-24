package com.leapfinance;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class FileSystem  implements MyFileSystem{

    private final long MAX_FILE_SIZE = 1024*1024*1024;
    private File myfile = null;
    private String pathname = "myFile.txt";


    public FileSystem(String filename) throws Exception{

        try {

            myfile = new File(pathname);
            if(myfile.exists() == false) myfile.createNewFile();



        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

    }

    public FileSystem(String filename,String pathname) throws Exception{

        this(filename);
    }

    public  void create(String key,String value, long timeToLive) throws Exception{

        RandomAccessFile random = null ;
        FileChannel fc = null;
        FileLock lock = null;


        try {

            if(key.length() > 32){
                throw new Exception("key is larger than 32 characters");
            }

            if(value.getBytes(StandardCharsets.UTF_8).length > 16 * 1024){
                throw new Exception("value is larger than 16KB ");
            }

            random = new RandomAccessFile(myfile,"rw");
            fc = random.getChannel();
            while(lock == null){
                try{
                    lock = fc.lock();

                }catch(Exception e){

                }
            }

            deleteKey(random,null);

            long newsize = random.length() + (long)key.getBytes().length + (long)value.getBytes().length;
            if(newsize > MAX_FILE_SIZE)
                throw new Exception("your data store exceeded maximum file size limit ");


            if (ifKeyExist(random,key) == true)
               throw new Exception("key already exists");


            random.seek(random.length());
            random.writeBytes(key); random.writeBytes("\n");
            random.writeBytes(value); random.writeBytes("\n");
            random.writeBytes(liveTime(timeToLive + System.currentTimeMillis())); random.writeBytes("\n");



        } catch (IOException e){
             throw e;
        } finally {
            try {
                if(lock != null)
                lock.release();
                fc.close(); random.close();
            }catch(Exception e){
                throw e;
            }

        }

        return ;

    }

    public  void delete(String key) throws Exception{

        RandomAccessFile random = null;
        FileChannel fc = null;
        FileLock lock = null;

        try {


            random = new RandomAccessFile(myfile,"rw");
            fc =  random.getChannel();

            while(lock == null){
                try{
                    lock = fc.lock();

                }catch(Exception e){

                }
            }

            deleteKey(random,key);

            }
        catch (IOException e){
            throw e;
        }finally {
            try {
                if(lock != null)
                lock.release();
                fc.close();
                random.close();
            }catch(Exception e){
                throw e;
            }
        }

        return ;
    }

    public  String read(String key) throws Exception {

        RandomAccessFile random = null;
        FileChannel fc = null;
        FileLock lock = null;

        String currentTime = liveTime(System.currentTimeMillis());
        String value , timeToLive , returnValue=null ;


        try {
            random = new RandomAccessFile(myfile,"rw");
            fc = random.getChannel();

            while(lock == null){
                try{
                    lock = fc.lock();

                }catch(Exception e){

                }
            }

            random.seek(0);
            String line ;

            while((line = random.readLine()) != null) {
                value = random.readLine();
                timeToLive = random.readLine();

                if (line.equals(key) && timeToLive.compareTo(currentTime) > 0 ) {
                   returnValue = value;
                    break;
                }
            }

        } catch (IOException e){
                throw e;

        } finally {
            try {
                if(lock != null)
               lock.release();
               fc.close(); random.close();
            }catch(Exception e){
              throw e;
            }
        }

        return returnValue;

    }

    // helper functions
   private boolean ifKeyExist(RandomAccessFile random,String key) throws  Exception{

       boolean success = false;
        try {
            random.seek(0);

            String line;
            while((line = random.readLine()) != null) {
                if (line.equals(key) ) {
                    success = true;
                    break;
                }
                    random.readLine();
                    random.readLine();
            }

        } catch (IOException e){
           throw e;

        } finally {

        }

        return success;

    }


   private void deleteKey(RandomAccessFile random,String key) throws Exception{

        String currentTime = liveTime(System.currentTimeMillis());

        try{
            random.seek(0);
            String line;
            LinkedList<String> ls = new LinkedList<>();

            while( (line = random.readLine()) != null) {
                String value = random.readLine();
                String timeToLive = random.readLine();

                if( timeToLive.compareTo(currentTime) < 0) continue;

                if ( line.equals(key) ) continue;


                ls.add(line);
                ls.add(value);
                ls.add(timeToLive);
            }


            random.setLength(0);
            random.seek(0);

            while(ls.size() > 0){
                random.writeBytes(ls.pollFirst());random.writeBytes("\n");
                random.writeBytes(ls.pollFirst());random.writeBytes("\n");
                random.writeBytes(ls.pollFirst());random.writeBytes("\n");
            }

        } catch (Exception e){
            throw e;
        }

    }

   private String liveTime(long time){
        return String.valueOf(time);
    }


}


