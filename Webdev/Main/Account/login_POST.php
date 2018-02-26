<?php session_start(); ?>
<?php
    header("Location: ../index.php");
    
    $email = $_POST['email'];
    $pw = $_POST['password'];
 
    
    
    $link = mysqli_connect("127.0.0.1", "root", "swater0", "taoyuan");
    
    $sql = "SELECT id,password_hashed FROM Member where email=".$email;
    $result = $link->query($sql);
    
    $row = mysqli_fetch_assoc($result);
    
    $pswd=$row['password_hashed'];

    if(password_verify($pw,$pswd))
    {
        echo "good";
         $_SESSION['UUID'] = $row['id'];
    }

    
    mysqli_close($link);
    
     exit();
    
    ?>

