
$(document).ready(function(){

    //NEW!!!!!!
    if ( typeof PageLanguageChanger != "undefined")
        PageLanguageChanger();

    $('form').on('submit',function(e){
        e.preventDefault();
        //console.log(this)


        let send = {
            email: $('form')[0][0].value,
                password: $('form')[0][1].value,
        };
        //console.log(JSON.stringify(send))
        $.ajax({
            type     : "POST",
            cache    : false,
            url      : "api/v1/login/",
            data     : JSON.stringify(send),
            contentType: "application/json",

            success: function(data, textStatus, jqXHR) {
                location.replace("dashboard");
            },
            error: function(jqXHR, status, err) {
                $(".invalid-feedback").show();
            }
        });
    });

    $(".input-group-append .btn").on('click', function(event) {
        event.preventDefault();
        if($('#inputPassword').attr("type") == "text"){
            $('#inputPassword').attr('type', 'password');
            $('.btn i').addClass( "fa-eye-slash" );
            $('.btn i').removeClass( "fa-eye" );
        }else if($('#inputPassword').attr("type") == "password"){
            $('#inputPassword').attr('type', 'text');
            $('.btn i').removeClass( "fa-eye-slash" );
            $('.btn i').addClass( "fa-eye" );
        }
    });

    var tooltipsUpdate = () => {
        if ( $('[data-toggle="tooltip"]').tooltip !== null && $('[data-toggle="tooltip"]').tooltip !== undefined)
            $('[data-toggle="tooltip"]').tooltip({
                trigger : 'hover'
            });  
    }
    tooltipsUpdate();
    
    NavbarLogic.getInstance();

    //new bug fix
    var resizeWindow = () => {
    
        $("html").height("100%");
        $("html").height($(document).height());
    } 
	window.onresize = resizeWindow;
	resizeWindow();
});