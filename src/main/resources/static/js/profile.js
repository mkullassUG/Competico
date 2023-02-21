const ProfileModule = (function($) {


    var Ajax = function(){
        var self = {};
 
        self.postNewGroup = (groupName, callback) => {

            var send = {groupName:groupName}
            return $.ajax({
                type     : "POST",
                cache    : false,
                url      : "/api/v1/groups",
                contentType: "application/json",
                data     : JSON.stringify(send),
                success: function(data, textStatus_, jqXHR_) {
                    if (self.debug) {
                        console.log("postNewGroup success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (self.debug) {
                        console.log("postNewGroup error");
                        console.log(data);
                    }
                    if ( callback )
                        callback(false, data.responseText);
                }
            });
        } 

        self.getGroupNames = (callback) => {

            return $.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/names",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (self.debug) {
                        console.log("getGroupNames success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getGroupNames error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        } 

        self.getAllRequests = (callback) => {

            return $.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/requests/all",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (self.debug) {
                        console.log("getMyRequests success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (self.debug) {
                        console.warn("getMyRequests error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.postReadMessage = (id, callback) => {
            var send = JSON.stringify({messageRead:true});

            return $.ajax({
                type     : "POST",
                cache    : false,
                url      : "/api/v1/groups/message/"+id+"/read",
                contentType: "application/json",
                data     : send,
                success: function(data, textStatus_, jqXHR_) {
                    if (self.debug) {
                        console.log("postReadMessage success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (self.debug) {
                        console.warn("postReadMessage error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false, data.responseText);
                }
            });
        }
        
        self.getJoinRequestsCount = (callback) => {
            
            return $.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/joinrequests/count",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (self.debug) {
                        console.log("getJoinRequestsCount success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (self.debug) {
                        console.warn("getJoinRequestsCount error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.getUnreadMessagesCount = (callback) => {
            
            return $.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/messages/unread/count",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (self.debug) {
                        console.log("getJoinRequestsCount success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (self.debug) {
                        console.warn("getJoinRequestsCount error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.getUnreadMessages = (callback) => {
            
            return $.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/messages/unread",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (self.debug) {
                        console.log("getJoinRequestsCount success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (self.debug) {
                        console.warn("getJoinRequestsCount error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.getGroupLobbiesFrom = (code, callback) => {
                
            return $.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/"+code+"/lobbies",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (self.debug) {
                        console.log("getJoinRequestsCount success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (self.debug) {
                        console.warn("getJoinRequestsCount error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.getGroupLobbies = (callback) => {
            
            return $.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/lobbies",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (self.debug) {
                    console.log("getGroupLobbies success");
                    console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (self.debug) {
                        console.warn("getGroupLobbies error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        return self;
    }();

    const ProfileLogic = (accountInfo_, debug = false, depMocks = {}, successfulCreationCallback) => {
    
        /* singleton */
        if (ProfileLogic.singleton)
            return ProfileLogic.singleton;
        var self = {};
        if (!ProfileLogic.singleton && accountInfo_)
            ProfileLogic.singleton = self;
        else if (!ProfileLogic.singleton && !accountInfo_) 
            return ProfileLogic.getInstance(false, debug, depMocks, successfulCreationCallback);
    
        /* environment preparation */
        window = typeof window != 'undefined'? window : depMocks.windowMock;
    
        if ( depMocks.NavbarLogic && typeof NavbarLogic == "undefined")
            NavbarLogic = depMocks.NavbarLogic;
        if ( depMocks.PageLanguageChanger && typeof PageLanguageChanger == "undefined")
            PageLanguageChanger = depMocks.PageLanguageChanger;
        /*       logic variables          */
        var self = accountInfo_;
        self.nickname;
        self.username;
        self.email;
        self.emailVerified;
        self.roles;
        self.authenticated;
        self.myScore;
        self.emailResponseAwait = false;
        self.emailResponseAwaitTimeout;
        self.moznaProbowac = true;
        self.isMe = true;
        Ajax.debug = debug;
        var groupNameRegex = /^[a-zA-Z0-9ąĄłŁśŚćĆńŃóÓżŻźŹęĘ ./<>?;:"'`!@#$%^&*()[\]{}_+=|\\\-]{2,32}$/;
        
        /*       logic functions          */
        self.profileInit = (accountInfo) => {
                
            var setupProfile = (profileUserData) => {
                
                self.nickname = profileUserData.nickname;
                self.username = profileUserData.username;
                self.roles = profileUserData.roles?profileUserData.roles:[];
    
                if (typeof NavbarLogic != "undefined")
                    NavbarLogic(profileUserData, debug);
                if ( typeof PageLanguageChanger != "undefined")
                    PageLanguageChanger(false, debug, {}, self.InitWithPageLanguageChanger);
                
                if ( self.isMe )
                    self.ajaxGetRelativeLeaderboard(self.setupMyScoreFromRelativeLeaderboard);
                else if ( profileUserData.hasRating === false)
                    self.setupMyScoreEmpty();
                else
                    self.setupMyScore(profileUserData.rating);
        
                $("#usernameDiv").text(self.username);
                $("#nicknameDiv").text(self.nickname);
                if ( profileUserData.email ) {
                    $("#emailDiv").text(self.email);
                    $("#newEmailInput").val(self.email);
                }
        
                if ( self.emailVerified ) {
                    $("#emailH6Text").addClass("emailVerifiedClass");
                    $("#sendEmailVerifyCol").hide();
                } else {
                    $("#emailH6Text").addClass("emailNotVerifiedClass");
                }
                
        
                if ( self.roles.includes("LECTURER")) {
                    $("#roleDiv").text("Lektor");
                    self.setupPageForLecturer();
                }
                else
                    $("#roleDiv").text("Gracz");
        
                if ( self.roles.includes("SWAGGER_ADMIN"))
                    $("#roleDiv").text("S-Admin");
                if ( self.roles.includes("TASK_DATA_ADMIN"))
                    $("#roleDiv").text("TD-Admin");
                if ( self.roles.includes("ACTUATOR_ADMIN"))
                    $("#roleDiv").text("A-Admin");
                
                listenersSetup();
                tooltipsUpdate();

                if ( MessagesModule )
                    MessagesModule().getInstance(false, (data)=>{data.setFunctionToInform(self.messagerFunction);});

                if (successfulCreationCallback)
                    successfulCreationCallback(true);
            }
            var UrlHasUsername = getOtherProfileUser();
            if ( UrlHasUsername && UrlHasUsername !== self.username )
                changeProfileToOther(UrlHasUsername, setupProfile);
            else {
                setupProfile(accountInfo);
                isMeInitGroupsAndMessages();
            }

            
        }
        
        self.InitWithPageLanguageChanger = (data, lang) => {

            if ( data === true){
                return;
            }
            
            if ( $("#sendEmailVerifyButton").text() === PageLanguageChanger().getTextFor("sendEmailVerifyButton2","pl") || $("#sendEmailVerifyButton").text() === PageLanguageChanger().getTextFor("sendEmailVerifyButton2","eng") ) {
                PageLanguageChanger().jsonData["#sendEmailVerifyButton"] = PageLanguageChanger().jsonData.OTHER_PAGE_ELEMENTS["sendEmailVerifyButton2"];
            }
            
        }

        /*       event listeners          */
        var listenersSetup = () => {
    
            self.stopHoverAnimation_nickname = false;
            self.stopHoverAnimation_email = false;
            var setupEventListenersForEdit = () => {
                self.stopHoverAnimation_nickname = false;
                self.stopHoverAnimation_email = false;
                if ($("#nicknameRow").length > 0) {
                    $("#nicknameRow").hover(
                        function () {
                            if (!self.stopHoverAnimation_nickname) {
                                $('#nicknameEditButt').stop(true, true).fadeOut();
                                $('#nicknameEditButt').fadeIn();
                            }
                        },
                        function () {
                            if (!self.stopHoverAnimation_nickname) {
                                $('#nicknameEditButt').fadeIn();
                                $('#nicknameEditButt').stop(true, true).fadeOut();
                            }
                        }
                    );
                }
                if ($("#emailRow").length > 0) {
                    $("#emailRow").on({
                        'mouseenter':function() {
                            if (!self.stopHoverAnimation_email && $('#emailEditButt').length > 0) {
                                $('#emailEditButt').stop(true, true).fadeOut();
                                $('#emailEditButt').fadeIn();
                            }
                            
                        },'mouseleave':function() {
                            if (!self.stopHoverAnimation_email && $('#emailEditButt').length > 0) {
                                $('#emailEditButt').fadeIn();
                                $('#emailEditButt').stop(true, true).fadeOut();
                            }
                        }
                    });
                }
    
                if ( $('#nicknameEditButt').length > 0 ) {
                    
    
                    $('#nicknameEditButt').on("click",
                        function () {
                            
                            if ( $("#nicknameInput").length > 0 )
                                createNicknameEditDiv();
                            else
                                createNicknameEditInput();
                        }
                    )
                }
    
                if ( $('#btnSendChangesEmail').length > 0 ) {
                    
                    $('#btnSendChangesEmail').on("click", (e) => {
                        e.preventDefault();
    
                        $("#invalidEmailInfoBad").hide();
                        $("#invalidEmailInfoIdentical").hide();
                        $("#invalidEmailInfoUsed").hide();
                        $("#invalidPasswordInfoEmail").hide();
                        var email = $("#newEmailInput").val();
                        var password = $("#oldPasswordInputEmail").val();
    
                        if ( email === "" && password === "") {
                            $('#saveChangesEmailModal').modal('hide');
                            return;
                        }
    
                        var isEmailValid = AccountValidation().emailValid(email);
                        if ( isEmailValid )
                            self.ajaxSendProfileEmailChanges(
                                self.afterEmailChangeAction,
                                email,
                                password
                            );
                        else 
                            $("#invalidEmailInfoBad").show();
                    })
    
                    
                }
    
                if ( $('#btnSendChangesPassword').length > 0 ) {
                    
                    $('#btnSendChangesPassword').on("click", (e) => {
                        e.preventDefault();
    
                        $("#invalidPasswordInfoPassword").hide();
                        $("#invalidNewPasswordInfoPassword").hide();
                        $("#invalidNewPasswordInfoPasswordIdentical").hide();
                        var newPassword = $("#newPasswordInput").val();
                        var password = $("#oldPasswordInputPassword").val();
    
                        if ( newPassword === "" && password === "") {
                            $('#saveChangesPasswordModal').modal('hide');
                            return;
                        }
    
                        var isPasswordValid = AccountValidation().passwordValid(newPassword);
                        if ( isPasswordValid )
                            self.ajaxSendProfilePasswordChanges(
                                self.afterPasswordChangeAction,
                                newPassword,
                                password
                            );
                        else
                            $("#invalidNewPasswordInfoPassword").show();
                    });
                }
            }
            setupEventListenersForEdit();
    
            if ( $("#btnSendChanges").length > 0 ) {
                $("#btnSendChanges").click(function() {
                    if ( $("#nicknameInput").length > 0) {
                        var nickname = $("#nicknameInput").val();
                        var isNicknameValid = AccountValidation().nicknameValid(nickname);
                        if ( isNicknameValid )
                            self.ajaxSendProfileNicknameChanges(self.afterNicknameChangeAction, nickname);
                        else {
                            $("#nicknameInput").css({"color":"red"})
                            setTimeout(function() {
                                $("#nicknameInput").css({"color":"initial"})
                            },2000);
                            if (debug)
                                console.warn("ERROR !!!");
                        }
        
                    }
                    
                    $("#saveChangesDiv").hide();
                })
            }
        
            if ( $('#emailEditButt').length > 0) {
                $('#emailEditButt').on('click',() => {
        
                    if ( $("#newEmailInput").length > 0)
                        $("#newEmailInput").val(self.email);
                })
            }
        
            if ( $("#sendEmailVerifyButton").length) {
                $("#sendEmailVerifyButton").on('click',()=>{

                    if ( self.emailResponseAwait )
                        return;
                    if ( self.moznaProbowac ) {
                        self.ajaxSendEmailVerification((data)=>{

                            self.emailResponseAwait = false;
                            $("#sendEmailVerifyButton")[0].disabled = false;
                            if ( data === false )
                                displayInfoAnimation("Email nie wysłany", false);
                            else 
                                displayInfoAnimation("Email wysłany", true);
                            
                            if ( $("#sendEmailVerifyButton").text() === ((typeof PageLanguageChanger != "undefined")?PageLanguageChanger().getTextFor("sendEmailVerifyButton2"):"Wyślij ponownie")) {
                                self.moznaProbowac = false;
                                setTimeout(function(){
                                    self.moznaProbowac = true;
                                    $("#sendEmailVerifyButton")[0].disabled = false;
                                },5 * 1000 * 60);
                            }
                            $("#sendEmailVerifyButton").text(((typeof PageLanguageChanger != "undefined")?PageLanguageChanger().getTextFor("sendEmailVerifyButton2"):"Wyślij ponownie"));
                            clearTimeout(self.emailResponseAwaitTimeout);
    
                        });
                        
                        self.emailResponseAwait = true;
                        $("#sendEmailVerifyButton")[0].disabled = true;

                        displayInfoAnimation("Wysłano zapytanie...", true);
                        self.emailResponseAwaitTimeout = setTimeout(function(){
                            displayInfoAnimation("Spróbuj ponownie później", false);
                        },5000);
                    } else {
                        $("#sendEmailVerifyButton")[0].disabled = true;
                        displayInfoAnimation("Poszukaj wiadomości w spamie <br> lub spróbuj ponownie za 5 minut.", false);
                    }
                    
                });
            }
        }
        
        var getOtherProfileUser = () => {
            var path = window.location.pathname.split("/");
            if ( path.length == 3 && path[2] != self.username && path[2] != "")
                return path[2];
            return false;
        }

        var changeProfileToOther = (username, callback) => {
            self.isMe = false;
            $(".editIconClassDiv").remove();
            $("#groupManagementDiv").hide();
            $("#passwordAndEmailButtonsRow").remove();
            $("#passwordRow").parent().find("hr").remove();
            $("#passwordRow").remove();
            $("#emailRow").remove();
            $("#saveChangesDiv").remove();

            self.ajaxGetOtherUserProfile(
                username,
                callback
            );
        }

        var tooltipsUpdate = () => {
            if ( $('[data-toggle="tooltip"]').tooltip !== null && $('[data-toggle="tooltip"]').tooltip !== undefined)
                $('[data-toggle="tooltip"]').tooltip({
                    trigger : 'hover'
                });  
        }
    
        self.setupMyScoreFromRelativeLeaderboard = (data) => {

            if (data.length <= 0)
                return;
                
            for (let i = 0; i < data.length; i++) {
    
                var player = data[i];
    
                var isMe = (player.username===self.username && player.nickname===self.nickname);
                if (isMe) {
                    self.myScore = player.rating;
                    break;
                }
            }
    
            if (self.myScore) {
                self.setupMyScore(self.myScore);
            } else {
                self.setupMyScoreEmpty()
            }
        }
      
        self.setupMyScore = (score) => {
            $("#scoreDiv").text("score: " + score);
        }
    
        self.setupMyScoreEmpty = () => {
            $("#scoreDiv").text("Lektor, nie uczestniczy w grach");
        }
    
        self.setupPageForLecturer = () => {
            $(".lectorEnableClass").show();
        }
    
        var createNicknameEditInput = () => {
            
            if ( $("#nicknameDiv").length > 0) {
    
                self.stopHoverAnimation_nickname = true;
                var currentValue = $("#nicknameDiv").text();
    
                var input = $(`<input id="nicknameInput" class="col-sm-7" value="`+currentValue+`"></input>`);
                $("#nicknameDiv").replaceWith(input);
    
                input.focus();
                var value = input.val();
                input.focus().val("").val(value);
    
                input.on("input propertychange",
                    function () {
                        showSaveButton();
                    }
                );
    
                input.blur(
                    function () {
                        if (  $("#nicknameInput").length > 0 && self.nickname == $("#nicknameInput").val()) {
                            var div = $(`<div id="nicknameDiv" class="col-sm-7 text-secondary">` + currentValue + `</input>`);
                            $("#nicknameInput").replaceWith(div);
                            self.stopHoverAnimation_nickname = false;
                            $('#nicknameEditButt').fadeOut();
                        }
                    }
                )
            }
        }
    
        var createNicknameEditDiv = () => {
            var div = $(`<div id="nicknameDiv" class="col-sm-7 text-secondary">`+ self.nickname + `</div>`);
    
            $("#nicknameInput").replaceWith(div);
            self.stopHoverAnimation_nickname = false;
            $('#nicknameEditButt').fadeOut();
        }
    
        self.afterNicknameChangeAction = (data, nickname) => {
            
            if ( $("#nicknameInput").length > 0) {
                if ( data === true ) { 
    
                    self.nickname = nickname;
                    createNicknameEditDiv();
                } else if (data === "BAD_NICKNAME"){
    
                    $("#nicknameInput").css({"color":"red"})
                    setTimeout(function() {
                        $("#nicknameInput").css({"color":"initial"})
                    },2000);
                } else {
                    if ( debug ) {
                        console.warn("Nickame change warning!");
                        console.warn(data);
                    }
    
                    $("#nicknameInput").css({"color":"red"})
                    setTimeout(function() {
                        $("#nicknameInput").css({"color":"initial"})
                    },2000);
                }
            }
        }
    
        self.afterEmailChangeAction = (data, email) => {
    
            
            if ( data === true ) {
                location.reload();
               
            } else if (data === "BAD_PASSWORD") {
                $("#invalidPasswordInfoEmail").show();
            } else if (data === "BAD_EMAIL") {
                $("#invalidEmailInfoBad").show();
            } else if (data === "USED_EMAIL") {
                $("#invalidEmailInfoUsed").show();
            } else if (data === "SAME_EMAIL") {
                $("#invalidEmailInfoIdentical").show();
            } else {
                if ( debug )
                    console.warn("Email change warning!");
            }
        }
    
        self.afterPasswordChangeAction = (data) => {
            
            
            if ( data === true ) {
                location.reload();
    
            } else if (data === "BAD_OLD_PASSWORD") {
    
                $("#invalidPasswordInfoPassword").show();
            } else if (data === "BAD_NEW_PASSWORD") {
    
                $("#invalidNewPasswordInfoPassword").show();
            } else if (data === "SAME_PASSWORD") {
    
                $("#invalidNewPasswordInfoPasswordIdentical").show();
            } else {
                if ( debug )
                    console.warn("Password change warning!");
            }
        }
    
        var showSaveButton = () => {
            if ( !areEmailAndNicknameSameAsCurrent() ) {
                $("#saveChangesDiv").show();
            } else {
                $("#saveChangesDiv").hide();
            }
        }
    
        var areEmailAndNicknameSameAsCurrent = () => {
    
            var emailInput = $("#emailInput");
            var nicknameInput = $("#nicknameInput");
    
            if ( emailInput.length > 0 && self.email != emailInput.val()) {
                return false
            }
    
            if (  nicknameInput.length > 0 && self.nickname != nicknameInput.val()) {
                return false;
            }
            return true;
        }
        
        var displayInfoAnimation = (text, success = true) => {
            var previousMessages = $(".failSuccessInfo");
            previousMessages.each((b,t)=>{
                $(t).css({marginTop: '+=50px'});
            });

            var failInfoDiv = $(`<div class="position-absolute failSuccessInfo alert alert-`+(success?"success":"danger")+`" style="right:0px;top:0px;">` + text + `</div>`)
            $("body").append(failInfoDiv)
        
            failInfoDiv.animate({
              top: "6%",
              opacity: 1
            }, 2000, function() {
              // Animation complete.
              setTimeout(function(){
                failInfoDiv.animate({
                  top: "10%",
                  opacity: 0
                }, 1000, function() {
                  // Second Animation complete.
                });
              },2000)
              
            });
        }


        /* messages */
        self.messagerFunction = (data) => {

            if ( data.areNew ) {
                //dźwiek
                //console.log("Ding!")
            }

            $("#allMessagesCount").text(
                (data.numberOfMessages?data.numberOfMessages:0)+
                (data.numberOfLobbies?data.numberOfLobbies:0)
                );
            isMeInitGroupsAndMessages();
            var updateNavbar = () => {
                $("#messageUnreadMessages").text(
                    (data.numberOfMessages+data.numberOfLobbies)?
                    ((data.numberOfMessages+data.numberOfLobbies)>99?
                    "99+":(data.numberOfMessages+data.numberOfLobbies)):"");
                $("#messageAwaitingRequests").text(data.numberOfRequests?(data.numberOfRequests>99?"99+":data.numberOfRequests):"");
            }
            updateNavbar();
        }

        var isMeInitGroupsAndMessages = () => {

            var requestArray = [
                Ajax.getUnreadMessages(),
                Ajax.getGroupLobbies(),
            ]

            if ( self.isLecturer ) {
                requestArray.push(Ajax.getAllRequests());
                requestArray.push(Ajax.getJoinRequestsCount());
                requestArray.push(Ajax.getGroupNames());
            }

            Promise.all(requestArray).then(data=>{

                var messages = data[0];
                self.groupLobbies = data[1]
                var groupTBody = $("#groupTBody");
                groupTBody.html('');

                var counter = 0;
                for ( let i = 0; i < self.groupLobbies.length; i++) {
                    var lobbyInfo = self.groupLobbies[i];

                    counter++;
                    var newTr = $(`<tr data-toggle="modal" data-target="#displayMessageLobbyGroupModal" aria-hidden="true">`),
                    tdIndex = $("<td>").text(i+1),
                    tdTitle = $("<td>").text("Lobby grupy: " + lobbyInfo.groupName),
                    tdCrea = $("<td>");
                    
                    newTr.addClass("bg-warning")

                    newTr[0].dataset["lobby"] = lobbyInfo.lobbyCode;

                    newTr.on('click',(e)=> {
                        var target = $(e.target);
                        if ( !target.is("tr") )
                            target = target.closest("tr");
                        var lobby = target[0].dataset["lobby"];
                        self.focusedMessagelobbyId = lobby;
                        self.readMessageLobby(lobbyInfo);
                    })
                    
                    newTr.append(tdIndex).append(tdTitle).append(tdCrea);
                    groupTBody.append(newTr);
                }

                self.messages = messages;
                for (let i = 0; i < messages.length; i++ ) {
                    var message = messages[i];

                    var tr, tdIndex, tdTitle, tdCrea;

                    tr = $(`<tr data-toggle="modal" data-target="#displayMessageGroupModal" aria-hidden="true">`);
                    tdIndex = $(`<td>`).text(i+1+counter);
                    tdTitle = $(`<td>`).text(message.title);
                    tdCrea = $(`<td>`).text(message.creationDate);

                    tr.on('click',(e)=> {
                        var target = $(e.target);
                        var id = target.closest("tr")[0].dataset["id"];
                        self.focusedMessageId = id;
                        self.readMessage(id)
                    });

                    tr[0].dataset['id'] = message.id;
                    tr.append(tdIndex).append(tdTitle).append(tdCrea);
                    groupTBody.append(tr);
                }

            }).catch(e=>{
                if ( debug )
                    console.warn(e);
            })
        }


        self.readMessage = (id) => {
            
            var message = self.messages.filter(m=>{
                if ( m.id === id )
                    return true;
                else
                    return false;
            })[0];

            $("#messageContentDisplay").html('');
            if ( message.content.split("[[href|").length == 2 && message.content.split("|]]").length == 2) {
                var span1 = $("<span>").text(message.content.split("[[href|")[0]);
                var span2 = $("<span>").text(message.content.split("|]]")[1]);
                var aHtml = $("<a>").text("Dołącz przez link").attr("href","/game/" + 
                message.content.split("[[href|")[1].split("|]]")[0])

                $("#messageContentDisplay").append(span1).append(aHtml).append(span2);
            } else {
                $("#messageContentDisplay").text(message.content).show();
            }

            $("#messageTitleDisplay").text(message.title).show();
            $("#messagefromDisplay").text(message.username).show();
            $("#messagefromGroupDisplay").text(" - " + message.groupName).show();
            $("#messageDateDisplay").text(" - " + message.creationDate).show();
            $("#messageDateEditedDisplay").text( (message.editDate?" - (" + message.editDate + ")*" : "")).show();
            $("#messageOkBtn").show();

            Ajax.postReadMessage(id);
        }

        self.readMessageLobby = (lobbyInfo) => {
            
            $("#messageLobbyContentDisplay").html('');

            var span1 = $("<span>").text("dołącz przez kod: " + lobbyInfo.lobbyCode + ", \n lub link: ");
            var aHtml = $("<a>").text(lobbyInfo.lobbyCode).attr("href","/game/" + lobbyInfo.lobbyCode)

            $("#messageLobbyTitleDisplay").text("Lobby grupy " + lobbyInfo.groupName)
            $("#messageLobbyContentDisplay").append(span1).append(aHtml);
        }

        /*     ajax http actions       */
        self.ajaxSendEmailVerification = (callback) => {
            
            $.ajax({
                type     : "POST",
                cache    : false,
                url      : "/api/v1/emailverification",
                contentType: "application/json",
                success: function(data) {
                    if (debug){
                        console.log("ajaxSendEmailVerification success");
                        console.log(data);
                    }
                    callback(true);
                },
                error: function(jqXHR, status, err) {
                    if (debug){
                        console.warn("ajaxSendEmailVerification error");
                        console.warn(jqXHR);
                    }
                    callback(false);
                }
            });
        }
    
        self.ajaxSendProfileNicknameChanges = (callback, nickname) => {
            send = nickname;
            $.ajax({
                type     : "PUT",
                cache    : false,
                url      : "/api/v1/account/nickname",
                contentType: "application/json",
                data     : send,
                success: function(data) {
                    if (debug){
                        console.log("ajaxSendProfileNicknameChanges success");
                        console.log(data);
                    }
                    callback(true, nickname);
                    displayInfoAnimation("Zmieniono nickname.", true);
                },
                error: function(jqXHR, status, err) {
                    if (debug){
                        console.warn("ajaxSendProfileNicknameChanges error");
                        console.warn(jqXHR);
                    }
                    callback(jqXHR.responseText, nickname);
                    displayInfoAnimation("Nie Zmieniono nickname.", false);
                }
            });
        }
    
        self.ajaxSendProfileEmailChanges = (callback, email, password) => {
    
            send = JSON.stringify({
                email: email,
                password: password
            });
    
            $.ajax({
                type     : "PUT",
                cache    : false,
                url      : "/api/v1/account/email",
                data     : send,
                contentType: "application/json",
                success: function(data) {
                    if (debug){
                        console.log("ajaxSendProfileEmailChanges success");
                        console.log(data);
                    }
                    callback(true, email);
                    displayInfoAnimation("Zmieniono email.", true);
                },
                error: function(jqXHR, status, err) {
                    if (debug){
                        console.warn("ajaxSendProfileEmailChanges error");
                        console.warn(jqXHR);
                    }
                    callback(jqXHR.responseText, email);
                    displayInfoAnimation("Nie zmieniono emaila.", false);
                }
            });
        }
    
        self.ajaxSendProfilePasswordChanges = (callback, newPassword, password) => {
    
            send = JSON.stringify({
                newPassword: newPassword,
                oldPassword: password
            });
    
            $.ajax({
                type     : "PUT",
                cache    : false,
                url      : "/api/v1/account/password",
                data     : send,
                contentType: "application/json",
                success: function(data) {
                    if (debug){
                        console.log("ajaxSendProfilePasswordChanges success");
                        console.log(data);
                    }
                    callback(true);
                    displayInfoAnimation("Zmieniono hasło.", true);
                },
                error: function(jqXHR, status, err) {
                    if (debug) {
                        console.warn("ajaxSendProfilePasswordChanges error");
                        console.warn(jqXHR);
                    }
                    callback(jqXHR.responseText);
                    displayInfoAnimation("Nie zmieniono hasła.", false);
                }
            });
        }
    
        /*   ajax http requests       */
        self.ajaxGetTopLeaderboard = (callback) => {
            
            $.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/game/leaderboard/top",
                contentType: "application/json",
                success: function(data) {
                    if (debug){
                        console.log("ajaxGetTopLeaderboard success");
                        console.log(data);
                    }
    
                    callback(data);
                },
                error: function(jqXHR, status, err) {
                    if (debug){
                        console.warn("ajaxGetTopLeaderboard error");
                    }
                }
            });
        }

        self.ajaxGetPlayerRating = (username, callback, otherData) => {
            
            $.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/player/"+username+"/rating",
                contentType: "application/json",
                success: function(data) {
                    if (debug){
                        console.log("ajaxGetPlayerRating success");
                        console.log(data);
                    }
                    otherData.rating = data.rating;
                    otherData.hasRating = data.hasRating;
                    callback(otherData);
                },
                error: function(jqXHR, status, err) {
                    if (debug){
                        console.warn("ajaxGetPlayerRating error");
                    }
                }
            });
        }
      
        self.ajaxGetRelativeLeaderboard = (callback) => {
            
            $.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/game/leaderboard/relative",
                contentType: "application/json",
                success: function(data) {
                    if (debug){
                        console.log("ajaxGetRelativeLeaderboard success");
                        console.log(data);
                    }
    
                    callback(data);
                },
                error: function(jqXHR, status, err) {
                    if (debug){
                        console.warn("ajaxGetRelativeLeaderboard error");
                    }
                }
            });
        }
        
        self.ajaxGetOtherUserProfile = (username, callback) => {

            $.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/account/"+username+"/info",
                contentType: "application/json",
                success: function(accountInfo, textStatus, jqXHR) {
                    if (debug){
                        console.log("ajaxGetOtherUserProfile success");
                        console.log(accountInfo);
                    }

                    self.ajaxGetPlayerRating(accountInfo.username, callback,accountInfo);
                },
                error: function(jqXHR, status, err) {
                    if (debug){
                        console.warn("ajaxGetOtherUserProfile error");
                    }
                    
                }
                });
        }


        /*  initalization  */
        self.profileInit(accountInfo_);
        
        return self;
    }
      
    ProfileLogic.getInstance = (dataLazy, debug = false, depMocks = {}, successfulCreationCallback) => {
    
        if (ProfileLogic.singleton)
            return ProfileLogic.singleton;
    
        window = typeof window != 'undefined'? window : depMocks.windowMock;
    
        var ajaxReceiveAccountInfo = ( ) => {
            $.ajax({
            type     : "GET",
            cache    : false,
            url      : "/api/v1/account/info",
            contentType: "application/json",
            success: function(accountInfo, textStatus, jqXHR) {
                if (debug) {
                    console.log("ajaxReceiveAccountInfo success");
                    console.log(accountInfo);
                }
                if (typeof accountInfo == 'string')
                    ProfileLogic.singleton = ProfileLogic({}, debug, depMocks, successfulCreationCallback);
                else 
                    ProfileLogic.singleton = ProfileLogic(accountInfo, debug, depMocks, successfulCreationCallback);
    
            },
            error: function(jqXHR, status, err) {
                if (debug){
                    console.warn("ajaxReceiveAccountInfo error");
                }
                if ( successfulCreationCallback )
                    successfulCreationCallback(false);
            }
            });
        }
    
        if ( dataLazy )
            ProfileLogic.singleton = ProfileLogic(dataLazy, debug, depMocks, successfulCreationCallback);
        else
            ajaxReceiveAccountInfo();
    
        return ProfileLogic.singleton;
    }

    return {
        ProfileLogic: ProfileLogic, 
        getInstance: ProfileLogic.getInstance
    }
})


if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {ProfileModule};