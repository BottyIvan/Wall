<?php include 'header.php'; ?>
<section id="dropTheFile" class="scheda">
<div id="drop-files" ondragover="return false"> <!-- ondragover for firefox -->
    Drop Images Here
</div>
 
<label class="myLabel">
    <span>Choose file</span>
    <input type="file" required/>
</label>
    
<div id="uploaded-holder">
    <div id="dropped-files">
        <div id="upload-button">
            <a href="#" class="upload"><i class="ss-upload"> </i> Upload!</a>
            <a href="#" class="delete"><i class="ss-delete"> </i></a>
            <span>0 Files</span>
        </div>
    </div>
    <div id="extra-files">
        <div class="number">
            0
        </div>
        <div id="file-list">
            <ul></ul>
        </div>
    </div>
</div>
 
<div id="loading">
    <div id="loading-bar">
        <div class="loading-color"> </div>
    </div>
    <div id="loading-content">Uploading file.jpg</div>
</div>
 
<div id="file-name-holder">
    <ul id="uploaded-files">
        <h1>Uploaded Files</h1>
    </ul>
</div>
</section>
<footer class="index-footer">
        <div id="so_foot">
            <ul class="social">
                <li><a href="https://twitter.com/bottyivan"><span class="sp_so" id="twitter"></span></a></li>
                <li><a href="https://plus.google.com/+IvanBotty"><span class="sp_so" id="plus"></span></a></li>
                <li><a href="https://www.youtube.com/user/bottydroid"><span class="sp_so" id="tub"></span></a></li>
            </ul>
        </div>
        <div id="text_foot">
            <a><strong>&copy2015 BottyIvan</strong> BETA</a>
        </div>
	</footer>