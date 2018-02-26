<!DOCTYPE html>
<html>
    <head>
        <?php include('../src/head.html'); ?>
    </head>
<body style="padding-top:150px">

        <form method="POST" action="register_POST.php">
        帳號：<input name="email" type="text" /></br>
        密碼：<input name="password" type="text" /></br>
        卡號：<input name="card_num" type="text" /></br>
        電話：<input name="phone" type="text" /></br>
        地址：<input name="address" type="text" /></br>
        店名：<input name="name" type="text" /></br>

        <input type="submit" value="確定送出" />
        </form>

    </body>
</html>


<?php
    $stringSRC='mypassword';
    
    ?>



