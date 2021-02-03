
$(document).ready(function(){
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
                console.log(data)
            }
        });
    });
    NavbarLogic.getInstance();
});