$(function(){
    $.getJSON('wall.json',function(data){
        console.log('success');
        $.each(data,function(i,emp){
            var img = new Image();
            img.src = emp.url.medium;
            img.setAttribute("class", "banner-img");
            img.setAttribute("alt", emp.name);
            document.getElementById("img-container").appendChild(img);
        });
    }).error(function(){
        console.log('error');
    });
});