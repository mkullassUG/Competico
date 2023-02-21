const IndexLogic = (accountInfo_, debug = false) => {
  
    /*       logic variables          */
    var self = accountInfo_;
    self.nickname;
    self.username;
    self.email;
    self.roles;
    self.authenticated;
  
    /*       logic functions          */
    self.indexLogic = (accountInfo) => {
    
        self.roles = accountInfo.roles?accountInfo.roles:[];
        NavbarLogic.singleton = NavbarLogic(accountInfo, debug);

        if ( typeof PageLanguageChanger != "undefined")
            PageLanguageChanger(false,false,{},self.InitWithPageLanguageChanger);

        if ( MessagesModule && self.roles.length )
          MessagesModule().getInstance(false, (data)=>{data.setFunctionToInform(self.messagerFunction);});
    }
    
    self.InitWithPageLanguageChanger = (data, lang) => {

      if ( data === true){
        return;
      }

      if (self.authenticated) {
        $("#indexHelloPlayer").text(
          data.OTHER_PAGE_ELEMENTS["#indexHelloPlayer"][lang][1] + 
          self.nickname + 
          data.OTHER_PAGE_ELEMENTS["#indexHelloPlayer"][lang][2]
        )

        $("#indexJoinGame").show();
      } else {
        $("#indexHelloPlayer").text(
          data.OTHER_PAGE_ELEMENTS["#indexHelloPlayer"][lang][0]
        );

        $("#indexLogin").show();
      }
    }

    /* messages */
    self.messagerFunction = (data) => {

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

    /*       event listeners          */
    if ( $("#indexJoinGame").length > 0 )
        $("#indexJoinGame").on("click",()=> {
            window.location.href='/lobby';
        })
    if ( $("#indexJoinGame").length > 0 )
        $("#indexLogin").on("click",()=> {
            window.location.href='/login';
        })
    /*     ajax http actions       */
    
    /*   ajax http requests       */
  
    /*  initalization  */
    self.indexLogic(accountInfo_);
    
    return self;
}
  
IndexLogic.getInstance = (debug = false) => {
  
    if (IndexLogic.singleton)
      return IndexLogic.singleton;
  
    var ajaxReceiveAccountInfo = ( ) => {
      $.ajax({
        type     : "GET",
        cache    : false,
        url      : "/api/v1/account/info",
        contentType: "application/json",
        success: function(accountInfo, textStatus, jqXHR) {
          if (debug){
            console.log("ajaxReceiveAccountInfo success");
            console.log(accountInfo);
          }
          if (typeof accountInfo == 'string')
            IndexLogic.singleton = IndexLogic({}, debug);
          else
            IndexLogic.singleton = IndexLogic(accountInfo, debug);
        },
        error: function(jqXHR, status, err) {
          if (debug){
            console.warn("ajaxReceiveAccountInfo error");
          }
        }
      });
    }
    
    ajaxReceiveAccountInfo();
}