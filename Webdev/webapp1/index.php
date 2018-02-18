<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
                <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css" integrity="sha384-rwoIResjU2yc3z8GV/NPeZWAv56rSmLldC3R/AZzGRnGxQQKnKkoFVhFQhNUwEyJ" crossorigin="anonymous">
                    <script src="https://code.jquery.com/jquery-3.1.1.slim.min.js" integrity="sha384-A7FZj7v+d/sdmMqp/nOQwliLvUsJfDHW+k9Omg/a/EheAdgtzNs3hpfag6Ed950n" crossorigin="anonymous"></script>
                    <script src="https://cdnjs.cloudflare.com/ajax/libs/tether/1.4.0/js/tether.min.js" integrity="sha384-DztdAPBWPRXSA/3eYEEUWrWCy7G5KFbe8fFjk5JAIxUYHKkDx6Qin1DkWx51bBrb" crossorigin="anonymous"></script>
                    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/js/bootstrap.min.js" integrity="sha384-vBWWzlZJ8ea9aCX4pEW3rVHjgjt7zpkNpZk+02D9phzyeVkE+jo0ieGizqPLForn" crossorigin="anonymous"></script>
                    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
                    
                     <link rel="stylesheet" type="text/css" href="basic.css">
                    
    </head>
    <body>
        
        <nav class="navbar navbar-toggleable-md navbar-light bg-faded fixed-top">
            <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <a class="navbar-brand" href="#">Navbar</a>
            
            <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav mr-auto">
                    <li class="nav-item active">
                        <a class="nav-link" href="#">Home <span class="sr-only">(current)</span></a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">Link</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link disabled" href="#">Disabled</a>
                    </li>
                </ul>
                <form class="form-inline my-2 my-lg-0">
                    <input class="form-control mr-sm-2" type="text" placeholder="Search">
                        <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
                        
                        </form>
            </div>
            <div class="navText">
                <h1>Start from this</h1>
            </div>
        </nav>
        
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
        
        <script>
            $("#chooseBtn").click(function(){
                                  $(".realbody").slideUp( "slow", function() {
                                                         // when Animation complete.
                                                         });
                                  });
            </script>
    </body>
</html>


