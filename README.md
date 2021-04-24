# FileSystem


Idea behind FileSystem library is to create a data store and store data in key-value pair fromat at a specified location of the system.

It provides three operations - create , read and delete.

1) create - A new key - value pair can be added to the data store where each key is unique .
            It maintains a exipary time for each key-value pair aftr which the key becomes invalid and deleted.
            If duplicate is inserted it returns "key already exists" error.
            Key string should be capped at 32 chars , otherwise it returns a "key is larger than 32 characters" error.
            Value string size should be capped at 16KB "value is larger than 16KB " error.
            key/value parameter cannot be null, othrwise "key/value cannot be null"
            
            
2) read  -  This operation return the value associated  with  the key , provided the key exists in the data sotre and is valid.
 
3) delete - This operation deletes the key from data store  , provided the key exists in the data sotre and is valid.

