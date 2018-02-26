<?php session_start(); ?>
<!DOCTYPE html>
<html>
    <head>
        <?php include('src/head.html'); ?>
    </head>
    <body>
        <div class="imageContainer">
            
            <div class="hero-image">
                <div class="hero-text">
                    <h1 style="font-size:50px">Only Sample,sample only</h1>
                    <p>Design Here</p>
                    <button>Contact me</button>
                </div>
            </div>
        </div>
        
        
        <div class="realbody">
            <div class="row typeIndex" >
                <div class="col-md-3">
                    <div class="itemBlock">
                        <div class="item-text">
                            <h1 style="font-size:20px">Only Sample,sample only</h1>
                            <p>Design Here</p>
                            <button class="chooseBtn">選擇</button>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 ">
                    <div class="itemBlock">
                        <div class="item-text">
                            <h1 style="font-size:20px">Only Sample,sample only</h1>
                            <p>Design Here</p>
                            <button class="chooseBtn">選擇</button>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 ">
                    <div class="itemBlock">
                        <div class="item-text">
                            <h1 style="font-size:20px">Only Sample,sample only</h1>
                            <p>Design Here</p>
                            <button class="chooseBtn">選擇</button>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 ">
                    <div class="itemBlock">
                        <div class="item-text">
                            <h1 style="font-size:20px">Only Sample,sample only</h1>
                            <p>Design Here</p>
                            <button class="chooseBtn">選擇</button>
                        </div>
                    </div>
                </div>
                
            </div>
        </div>
        <?php
            $id=$_SESSION['UUID'];
            echo "<hr>".$id;
            ?>


        <script>
            $("#chooseBtn").click(function(){
                                  $(".realbody").slideUp( "slow", function() {
                                                         // when Animation complete.
                                                         });
                                  });
            </script>
    </body>
</html>



