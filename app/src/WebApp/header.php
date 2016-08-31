<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="it-IT" xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Wall BETA | By BottyIvan</title>
    <meta charset="utf-8" />
	<link rel="SHORTCUT ICON" href="im/web_hi_res_512.png">
	<link rel="stylesheet" type="text/css" media="all" href="CSS/style.css">
    <link rel="stylesheet" type="text/css" media="all" href="CSS/resp.css">
    <link rel="stylesheet" type="text/css" href="CSS/font-awesome.min.css">
    <link rel="stylesheet" href="CSS/animate.css">
    <link rel="stylesheet" href="CSS/pacman.css">
    <link rel="stylesheet" href="CSS/wall_style.css">
	<!-- Google Font -->
	<link href='http://fonts.googleapis.com/css?family=Ubuntu' rel='stylesheet' type='text/css'>
	<link href='http://fonts.googleapis.com/css?family=Courgette' rel='stylesheet' type='text/css'>
    <link href='https://fonts.googleapis.com/css?family=Oswald' rel='stylesheet' type='text/css'>
    <link href='https://fonts.googleapis.com/css?family=Roboto:300' rel='stylesheet' type='text/css'>
    <link href='http://fonts.googleapis.com/css?family=Lato:900,400' rel='stylesheet' type='text/css'>
	<!-- size -->
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <!-- Mobile viewport optimized: j.mp/bplateviewport -->
 	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<!-- verfica YouTube -->
	<meta name="google-site-verification" content="Yca32Am9O_kmX4MhZS9HGf1SIpKL559O_qmU6633u4A" />
    <!-- YouTube PopUp -->
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
    <link rel="stylesheet" type="text/css" media="all" href="CSS/yt_pop_up.css">
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.min.js"></script>
    <script type="text/javascript" src="js/jquery.youtubepopup.js"></script>
    <script type="text/javascript">
        $(function () {
            $("a.youtube").YouTubePopup({ autoplay: 1 });
        });
    </script>
    <!-- Wow js -->
    <script src="js/wow.min.js"></script>
    <script>
        var wow = new WOW({
                offset:100,        // distance to the element when triggering the animation (default is 0)
                mobile:false       // trigger animations on mobile devices (default is true)
            });
            wow.init();
    </script>
    <!--Fonts Awesome -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    <!-- buttonToTop -->
    <script type="text/javascript">
        $(function(){
            $(document).on( 'scroll', function(){
                if ($(window).scrollTop() > 100) {
                    $('.scroll-top-wrapper').addClass('show');
                } else {
                    $('.scroll-top-wrapper').removeClass('show');
                }
            });
            $('.scroll-top-wrapper').on('click', scrollToTop);
        });
        function scrollToTop() {
            verticalOffset = typeof(verticalOffset) != 'undefined' ? verticalOffset : 0;
            element = $('body');
            offset = element.offset();
            offsetTop = offset.top;
            $('html, body#homeTop').animate({scrollTop: offsetTop}, 500, 'linear');
        }
    </script>
    <!-- wall json app -->
    <script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
    <script type="text/javascript">
            $(function(){
                $.getJSON('wall.json',function(data){
                    console.log('success');
                    $.each(data,function(i,emp){
                      var a = document.createElement("a");
                      a.setAttribute("alt", emp.name);
                      a.setAttribute("data-lightbox", "img");
                      a.setAttribute("data-title", emp.name)
                      a.setAttribute("href", emp.url.large);
                      var img = new Image();
                      img.src = emp.url.medium;
                      img.setAttribute("class", "banner-img");
                      img.setAttribute("alt", emp.name);
                      a.appendChild(img);
                      document.getElementById("img-container").appendChild(a);
                    });
                }).error(function(){
                    console.log('error');
                });
            });
     </script>
    <!-- loaiding animation page -->
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.5.2/jquery.min.js"></script>
    <script src="http://cdnjs.cloudflare.com/ajax/libs/modernizr/2.8.2/modernizr.js"></script>
    <script type="text/javascript">
    //paste this code under the head tag or in a separate js file.
	// Wait for window load
        $(document).ready(function() {
            setTimeout(function(){
                $('body').addClass('loaded');
                $('h1').css('color','#222222');
            }, 3000);

        });
    </script>
    <!-- upload file -->
    <link rel="stylesheet" type="text/css" href="CSS/style_upload.css" />
    <script src="js/jquery.js"></script>
    <script src="js/javascript.js"></script>
</head>
<body id="homeTop">
    <div id="loader-wrapper">
        <div id="loader"></div>
        <div class="loader-section section-left"></div>
        <div class="loader-section section-right"></div>
    </div>
    <header id="new_cool" class="new_cool">
		<div id="logo">
			<a class="logo" href="../index.php"><img class="home" src="im/logo.png"></a>
		</div>
	</header>