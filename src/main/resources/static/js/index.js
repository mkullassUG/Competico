const IndexLogic = (accountInfo_) => {
  
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

        //navbar preparation
        NavbarLogic.singleton = NavbarLogic(accountInfo, debug);

        if ( typeof PageLanguageChanger != "undefined")
            PageLanguageChanger(false,false,false,self.InitWithPageLanguageChanger);
    }
    
    self.InitWithPageLanguageChanger = (data, lang) => {
      //prepare when data comes

      if ( data === true){
        if ( debug )
          console.log("PageLanguageChanger done");
        return;
      }

      //NEW 2021-04-27 wymyslec jak
      if (self.authenticated) {
        //wyświetl przycisk od gry na środku ekranu
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
        //wyświetl przycisk od zalogowania się na środku ekranu
        //nie pokazuj navbara
      }
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
        // url      : "/api/v1/playerinfo",
        contentType: "application/json",
        success: function(accountInfo, textStatus, jqXHR) {
          if (debug){
            console.log("ajaxReceiveAccountInfo success");
            console.log(typeof accountInfo);
            console.log(accountInfo);
            console.log(textStatus);
            console.log(jqXHR);
          }
          if (typeof accountInfo == 'string') //nie zalogowany
            IndexLogic.singleton = IndexLogic({}, debug);
          else //zalogowany
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