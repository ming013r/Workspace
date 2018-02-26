<?php
    header("Location: ../index.php");
    
    
    $email=$_POST['email'];
    $password=$_POST['password'];
    $card_num=$_POST['card_num'];
    $phone=$_POST['phone'];
    $address=$_POST['address'];
    $name=$_POST['name'];
    
    $password_hashed=password_hash($password,PASSWORD_DEFAULT);
    
    
    $link = mysqli_connect("127.0.0.1", "root", "swater0", "taoyuan");
    
    $sql = "INSERT INTO Member (email,password_hashed,card_num,phone,address,name)
    VALUES ('$email','$password_hashed','$card_num','$phone','$address','$name')";
    
    if ($link->query($sql) === TRUE) {
        echo "New record created successfully";
    } else {
    echo "Error: " . $sql . "<br>" . $link->error;
    }
    
    
    mysqli_close($link);
    
    exit();
    
    ?>
