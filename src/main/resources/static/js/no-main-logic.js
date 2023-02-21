var messagerFunction = (data) => {

    if ( data.areNew ) {
        //dźwiek
        //console.log("Ding!")
    }
    
    var updateNavbar = () => {
        $("#messageUnreadMessages").text(
            (data.numberOfMessages+data.numberOfLobbies)?
            ((data.numberOfMessages+data.numberOfLobbies)>99?
            "99+":(data.numberOfMessages+data.numberOfLobbies)):"");
        $("#messageAwaitingRequests").text(data.numberOfRequests?(data.numberOfRequests>99?"99+":data.numberOfRequests):"");
    }
    updateNavbar();
}

if ( typeof PageLanguageChanger !== undefined )
    PageLanguageChanger(false, false, {});
if ( typeof NavbarLogic !== undefined )
    NavbarLogic.getInstance();
if ( typeof MessagesModule !== undefined )
    MessagesModule().getInstance(false, (data)=>{
        data.setFunctionToInform(messagerFunction);
    });


