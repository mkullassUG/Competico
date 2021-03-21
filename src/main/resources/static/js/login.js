
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
            //called when successful
            window.location.href = "dashboard";
            
            },

            error: function(jqXHR, status, err) {
            //called when there is an error

            //TODO error message on screen
            //"Jak będziesz to później implementował, znany error ma kod 400 i opis pod responseText"
            console.warn("status: " + jqXHR.status  + ": " + err );
            }
        });
    });
});