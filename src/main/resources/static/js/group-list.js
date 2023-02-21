const GroupListModule = (function(deps={}) {

    /* environment preparation */
    var debug = false;
    if ( deps.debug )
        debug = true;

    if ( typeof $ === 'undefined' && typeof deps.$ === 'undefined')
        throw Error("jQuery not defined");
    else if ( typeof $ != 'undefined' && typeof deps.$ === 'undefined' )
        deps.$ = $;

    if ( typeof window === 'undefined' && typeof deps.window === 'undefined')
        throw Error("window not defined");
    else if ( typeof window != 'undefined' && typeof deps.window === 'undefined' )
        deps.window = window;
    
    if ( typeof NavbarLogic === 'undefined' && typeof deps.NavbarLogic === 'undefined') 
        throw Error("NavbarLogic not defined");
    if ( typeof deps.NavbarLogic === "undefined" && typeof NavbarLogic != "undefined")
        deps.NavbarLogic = NavbarLogic;

        
    if ( typeof PageLanguageChanger === 'undefined' && typeof deps.PageLanguageChanger === 'undefined') 
        throw new Error("PageLanguageChanger not defined");
    if ( typeof deps.PageLanguageChanger === "undefined" && typeof PageLanguageChanger != "undefined")
        deps.PageLanguageChanger = PageLanguageChanger;
    
    var Ajax = function(){
        var self = {};

        self.getWhoAmI = ( callback ) => {
            
            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/playerinfo",
                contentType: "application/json",
                success: function(playerInfo, textStatus, jqXHR) {
                    if (debug){
                        console.log("getWhoAmI success");
                        console.log(playerInfo);
                    }
                    if ( callback )
                        callback(playerInfo);
                },
                error: function(jqXHR, status, err) {
                    if (debug){
                        console.warn("getWhoAmI error");
                        console.warn(jqXHR);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.getAccountInfo = (callback) => {
            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/account/info",
                contentType: "application/json",
                success: function(accountInfo, textStatus, jqXHR) {
                    if (debug) {
                        console.log("getAccountInfo success");
                        console.log(accountInfo);
                    }
                    if ( callback )
                        callback(accountInfo);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getAccountInfo error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }  

        self.getGroupLobbies = ( callback) => {
        
            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/lobbies",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("getGroupLobbies success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getGroupLobbies error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.postNewGroup = (groupName, callback) => {

            var send = {groupName:groupName}
            return deps.$.ajax({
                type     : "POST",
                cache    : false,
                url      : "/api/v1/groups",
                contentType: "application/json",
                data     : JSON.stringify(send),
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("postNewGroup success");
                        console.log(data);
                    }
                    var obj = {};
                    obj.groupCode = data;
                    obj.groupName = groupName;

                    if ( callback )
                        callback(obj);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("postNewGroup error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false, data.responseText);
                }
            });
        } 

        self.getGroupsPage = (page, callback) => {

            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/" + page,
                contentType: "application/json",
                success: function(pageInfo, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("getGroupsPage success");
                        console.log(pageInfo);
                    }
                    if ( callback )
                        callback(pageInfo);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getGroupsPage error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        } 

        self.getGroupNames = (callback) => {

            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/names",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
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

        self.postJoinGroup = (code, callback) => {

            return deps.$.ajax({
                type     : "POST",
                cache    : false,
                url      : "/api/v1/groups/join/" + code,
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("getGroupsPage success");
                        console.log(data);
                    }

                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getGroupsPage error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false, data.responseText);
                }
            });
        }

        self.deleteMyRequest = (code, callback) => {

            return deps.$.ajax({
                type     : "DELETE",
                cache    : false,
                url      : "/api/v1/groups/join/" + code,
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("deleteMyRequest success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("deleteMyRequest error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.getMyRequests = (callback) => {

            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/requests/my",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("getMyRequests success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getMyRequests error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.getAllRequests = (callback) => {

            return deps.$.ajax({
                type     : "GET",
                cache    : false,
                url      : "/api/v1/groups/requests/all",
                contentType: "application/json",
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("getAllRequests success");
                        console.log(data);
                    }
                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("getAllRequests error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false);
                }
            });
        }

        self.postJoinRequest = (id, accept, callback) => {
            
            var send = JSON.stringify({accept:accept});
            return deps.$.ajax({
                type     : "POST",
                cache    : false,
                url      : "/api/v1/groups/requests/"+id+"/respond",
                contentType: "application/json",
                data     : send,
                success: function(data, textStatus_, jqXHR_) {
                    if (debug) {
                        console.log("postJoinRequest success");
                        console.log(data);
                    }

                    if ( callback )
                        callback(data);
                },
                error: function(data, status, err) {
                    if (debug) {
                        console.warn("postJoinRequest error");
                        console.warn(data);
                    }
                    if ( callback )
                        callback(false, data.responseText);
                }
            });
        }


        return self;
    }();

    const GroupListLogic = (data, successfulCreationCallback) => {

        /*  singleton   */
        if (GroupListLogic.singleton)
            return GroupListLogic.singleton;
        var self = {};
        if (!GroupListLogic.singleton && data)
            GroupListLogic.singleton = self;
        else if (!GroupListLogic.singleton && !data) 
            return GroupListLogic.getInstance(null, successfulCreationCallback);
    
        /*       logic variables          */
        self.playerInfo = data.account;
        self.groups = data.pageData.groups;
        self.pageNum = data.pageData.pageNum;
        self.debug = debug;
        self.isLecturer;
        self.nextPageNum = (data.pageData.groupsNext.length)? data.pageData.pageNum+1: false;
        self.prevPageNum = (data.pageData.pageNum == 1)? false: data.pageData.pageNum-1;
        self.groupNames = data.pageData.groupNames?data.pageData.groupNames:[];
        self.currentPageGroupNames = self.groups.map(g => {return g.name;}) 
        self.currentNewGroupCode;
        self.currentNewGroupName;
        self.requestsViewActive = false;
        self.requests = data.requests;
        self.lobbies = data.lobbies;
        var groupNameRegex = /^[a-zA-Z0-9ąĄłŁśŚćĆńŃóÓżŻźŹęĘ ./<>?;:"'`!@#$%^&*()[\]{}_+=|\\\-]{2,32}$/;
        
        /*       logic functions          */
        var GroupListLogicInit = (initData_) => {
            
            deps.PageLanguageChanger(false, debug, deps, self.InitWithPageLanguageChanger);

            if ( self.playerInfo.accountInfo.roles.includes("LECTURER") ) {
                setupLecturerPage();
            } else {
                setupPlayerPage();
            }

            if ( self.nextPageNum )
                deps.$("#nextPageNumbtn").text(self.nextPageNum).parent().removeClass('invisible').on('click',(e)=>{
                    deps.window.location.replace("/groups/"+self.nextPageNum);
                });

            if ( self.prevPageNum )
                deps.$("#prevPageNumbtn").text(self.prevPageNum).parent().removeClass('invisible').on('click',(e)=>{
                    deps.window.location.replace("/groups/"+self.prevPageNum);
                });
            
            if ( !self.currentPageGroupNames.length )
                deps.$("#groupTBody").html(`<div class="text-center noGroups"><div>`+
                ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("noGroups"):"Brak grup")
                    +`</div></div>`);

            deps.$(".pageNum").text(self.pageNum);
            
            tooltipsUpdate(); 
    
            deps.NavbarLogic(self.playerInfo.accountInfo, debug, deps);
    
            listenersSetup();
            
            if ( typeof MessagesModule !== undefined )
                MessagesModule(deps).getInstance(false, (data)=>{data.setFunctionToInform(self.messagerFunction);});

            if (successfulCreationCallback)
                successfulCreationCallback(self);
        }
        
        self.InitWithPageLanguageChanger = (data, lang) => {

            if ( deps.$(".noGroups").length )
                deps.$(".noGroups").text((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("noGroups"):"Brak grup");
        }

        /*       event listeners          */
        var listenersSetup = () =>{
            
            deps.window.onresize = self.resizeWindow;

            if (deps.$("#btnCreateGroup").length) {
                deps.$("#btnCreateGroup").on('click',(e)=> {
                    self.createNewGroup(self.currentNewGroupName);
                });
            }
            if (deps.$("#newGroupInput").length) {
                deps.$("#newGroupInput").on('change input click',(e)=> {
                    var input = deps.$(e.target);

                    self.updadeValidationInfoTorGroupInput(input.val().trim());
                });
            }
            
            deps.$("#newGroupInput").keydown(e=> {
                var code = e.keyCode || e.which;
                if ( code == 13 ) { 
                    self.createNewGroup(self.currentNewGroupName);
                }
            })

            if (deps.$("#codeGroupInput").length) {
                deps.$("#codeGroupInput").on('change input click',(e)=> {
                    var input = deps.$(e.target);

                    self.updadeValidationInfoTorGroupCodeInput(input.val().trim());
                });
            }
            
            deps.$("#codeGroupInput").keydown(e=> {
                var code = e.keyCode || e.which;
                if ( code == 13 ) { 
                    self.joinGroup(self.currentNewGroupCode);
                }
            });

            if( deps.$("#btnCreateGroupModalShow").length )
                deps.$("#btnCreateGroupModalShow").on('click',(e)=>{
                    self.getAllGroupNamesIfNull();
                    setTimeout(function(){deps.$("#newGroupInput").focus()},500);
                });

            if( deps.$("#btnJoinGroupModalShow").length )
                deps.$("#btnJoinGroupModalShow").on('click',(e)=>{
                    setTimeout(function(){deps.$("#codeGroupInput").focus()},500);
                });
            if ( deps.$("#btnjoinGroup").length) {
                deps.$("#btnjoinGroup").on('click', e=> {
                    self.joinGroup(self.currentNewGroupCode);
                })
            }

            if ( deps.$("#btnGroupsView").length )
                deps.$("#btnGroupsView").on('click', e => {
                    if ( !self.groupsViewActive)
                        self.switchToView("Groups");
                });

            if ( deps.$("#btnMyRequestView").length )
                deps.$("#btnMyRequestView").on('click', e => {
                    if ( !self.requestsMyViewActive)
                        self.switchToView("RequestsMy");
                });

            if ( deps.$("#btnAllRequestView").length )
                deps.$("#btnAllRequestView").on('click', e => {
                    if ( !self.requestsAllViewActive)
                        self.switchToView("RequestsAll");
                });

            if ( deps.$("#btnCancelRequestGroup").length ) {
                deps.$("#btnCancelRequestGroup").on('click', e => {
                    Ajax.deleteMyRequest(self.focusedGroupRequest, self.afterCancelMyRequest);
                });
            }

            if ( deps.$("#btnAcceptGroup, #modalAcceptRequestBtn").length ) {
                deps.$("#btnAcceptGroup, #modalAcceptRequestBtn").on('click', e => {
                    Ajax.postJoinRequest(self.focusedRequestId, true, self.afterAcceptRequest);
                    
                });
            }

            if ( deps.$("#btnDeclineGroup, #modalDeclineRequestBtn").length ) {
                deps.$("#btnDeclineGroup, #modalDeclineRequestBtn").on('click', e => {
                    Ajax.postJoinRequest(self.focusedRequestId, false, self.afterDeclineRequest);
                    
                });
            }
        }

        /* messages */
        self.messagerFunction = (data) => {

            if ( data.areNew ) {
                //dźwiek
                //console.log("Ding!")
            }

            self.lobbies = data.lobbies;
            
            var updatePage = () => {

                var requestArray = [
                    Ajax.getMyRequests()
                ];

                if ( self.isLecturer )
                    requestArray.push( Ajax.getAllRequests());

                Promise.all(requestArray).then((values)=>{
                
                    self.requests.my = values[0];
                    if ( self.isLecturer )
                        self.requests.all = values[1];
                    
                    if ( self.isLecturer ) {
                        setupLecturerPage();
                    } else {
                        setupPlayerPage();
                    }
                }).catch(e=> {
                    if ( debug )
                        console.log(e);
                })
            }
            updatePage();

            var updateNavbar = () => {
                deps.$("#messageUnreadMessages").text(
                    (data.numberOfMessages+data.numberOfLobbies)?
                    ((data.numberOfMessages+data.numberOfLobbies)>99?
                    "99+":(data.numberOfMessages+data.numberOfLobbies)):"");
                deps.$("#messageAwaitingRequests").text(data.numberOfRequests?(data.numberOfRequests>99?"99+":data.numberOfRequests):"");
                deps.$("#lobbyGamesCountInfo").text(data.numberOfLobbies?(data.numberOfLobbies>99?"99+":data.numberOfLobbies):"");
            }
            updateNavbar();
        }

        self.afterAcceptRequest = (data) => {
            
            if ( data === false) {
                displayInfoAnimation(
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgAcceptRequestError"):"Nie udało się akceptować prośby"), 
                    false);
                return;
            }
            displayInfoAnimation(
                ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgAcceptRequestSuccess"):"Prośba potwierdzona"), 
                true);

            Ajax.getAllRequests((data) => {
                self.requests.all = data;
                setupLecturerPage();
            });
            
        }

        self.afterDeclineRequest = (data) => {
            
            if ( data === false) {
                displayInfoAnimation(
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgRemoveRequestError"):"Nie udało się usunąć prośby"), 
                    false);
                return;
            }
            displayInfoAnimation(
                ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgRemoveRequestSuccess"):"Prośba usunięta"), 
                true);

            Ajax.getAllRequests((data) => {
                self.requests.all = data;
                setupLecturerPage();
            });
        }

        self.afterCancelMyRequest = (data) => {
            
            if ( data === false) {
                displayInfoAnimation("Nie udało się anulować prośby", false);
                return;
            }
            displayInfoAnimation(
                ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgRemoveRequestSuccess"):"Prośba usunięta"), 
                true);

            Ajax.getMyRequests((data)=> {
                self.requests.my = data;
                if ( self.isLecturer ) {
                    setupLecturerPage();
                } else {
                    setupPlayerPage();
                }
            })

        }

        self.createNewGroup = (groupName) => {
            
            if (groupName) {
                Ajax.postNewGroup(groupName,self.afterCreatingNewGroup)
            }

        }

        self.afterCreatingNewGroup = (data) => {

            if ( data === false) {
                displayInfoAnimation(
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgCreateGroupError"):"Nie stworzono grupy"),
                    false);
                return;
            } else if ( typeof data === 'string') {
                displayInfoAnimation("Nie stworzono grupy: " + data,false);
                return;
            }

            displayInfoAnimation(
                ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgCreateGroupSuccess"):"Stworzono grupę"), 
                true);
            self.groups.push({
                "name": data.groupName,
                "groupCode": data.groupCode,
                "creationDate": "teraz",
                "memberCount": 1
            })
            
            deps.window.location.replace("/group/" + data.groupCode);
        }
        
        self.updadeValidationInfoTorGroupInput = (groupName) => {

            deps.$("#invalidGroupInfoExists").hide();
            deps.$("#invalidGroupInfoBad").hide();
            deps.$("#newGroupInput").css("color","initial");
            var isValid = true;
            if (self.checkIfGroupExists(groupName)) {
                isValid = false;
                deps.$("#invalidGroupInfoExists").show();
                deps.$("#newGroupInput").css("color","red");
            } else if ( !validateGroupName(groupName) && groupName != ""){
                
                isValid = false;
                deps.$("#invalidGroupInfoBad").show();
                deps.$("#newGroupInput").css("color","red");
            } 
            if (isValid)
                self.updateGroupName(groupName);
            else 
                self.updateGroupName(isValid);
        }

        self.updadeValidationInfoTorGroupCodeInput = (groupCode) => {

            deps.$("#invalidGroupCodeInfoAlreadySent").hide();
            deps.$("#invalidGroupCodeInfoExists").hide();
            deps.$("#invalidGroupCodeInfoBad").hide();

            if ( groupCode.length === 9) {
                deps.$("#btnjoinGroup")[0].disabled = false;
                self.currentNewGroupCode = groupCode;
            } else {
                deps.$("#btnjoinGroup")[0].disabled = true;

                if ( groupCode.length !== 0)
                    deps.$("#invalidGroupCodeInfoBad").show();
            }
        }

        self.joinGroup = (code) => {

            Ajax.postJoinGroup(code,self.afterJoiningGroup)
        }
        self.afterJoiningGroup = (data, res) => {

            if ( data === false && res === "ALREADY_REQUESTED") {
                displayInfoAnimation(
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgSendRequestError"):"Nie wysłano prośby(oczekuje na potwierdzenie)"), 
                    false);
                deps.$("#invalidGroupCodeInfoAlreadySent").show();
                return;
            } else if ( data === false) {
                displayInfoAnimation("Nie wysłano zaproszenia (nieporpawny kod)", false);
                deps.$("#invalidGroupCodeInfoExists").show();
                return;
            }
            displayInfoAnimation(
                ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("msgSendRequestSuccess"):"Wysłano prośbę"), 
            true);

            deps.$("#joinGroupModal").modal("hide");
            deps.$("#codeGroupInput").val("");
            
            Ajax.getMyRequests((data)=> {
                self.requests.my = data;
                if ( self.isLecturer ) {
                    setupLecturerPage();
                } else {
                    setupPlayerPage();
                }
            });
        }

        self.currentlySwitchingFades = false;
        self.lastSwitchTo;
        self.switchToView = (mode) => {

            self.lastSwitchTo = mode;
            if (self.currentlySwitchingFades)
                return;
            self.lastSwitchTo = undefined;

            switch ( mode ) {
                case "RequestsMy":
                    deps.$('.pagiBtnHolder').attr('style','display:none !important');

                    self.requestsMyViewActive = true;
                    self.requestsAllViewActive = false;
                    self.groupsViewActive = false;
                    deps.$("#btnAllRequestView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnGroupsView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnMyRequestView").addClass("btn-primary active").removeClass("btn-secondary");
                    self.currentlySwitchingFades = true;

                    if ( deps.$("#allRequestsTable").is(":visible") )
                        deps.$("#allRequestsTable").fadeOut().promise().done(function(){
                            self.currentlySwitchingFades = false;
                            deps.$("#myRequestsTable").fadeIn();
                            if ( self.lastSwitchTo )
                                self.switchToView(self.lastSwitchTo);
                        });
                    else
                        deps.$("#allMyGroupsTable").fadeOut().promise().done(function(){
                            self.currentlySwitchingFades = false;
                            deps.$("#myRequestsTable").fadeIn();
                            if ( self.lastSwitchTo )
                                self.switchToView(self.lastSwitchTo);
                        });
                    break;
                case "RequestsAll":
                    deps.$('.pagiBtnHolder').attr('style','display:none !important');
                    self.requestsMyViewActive = false;
                    self.requestsAllViewActive = true;
                    self.groupsViewActive = false;
                    deps.$("#btnGroupsView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnMyRequestView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnAllRequestView").addClass("btn-primary active").removeClass("btn-secondary");
                    self.currentlySwitchingFades = true;

                    if ( deps.$("#allMyGroupsTable").is(":visible") )
                        deps.$("#allMyGroupsTable").fadeOut().promise().done(function(){
                            self.currentlySwitchingFades = false;
                            deps.$("#allRequestsTable").fadeIn();
                            if ( self.lastSwitchTo )
                                self.switchToView(self.lastSwitchTo);
                        });
                    else
                        deps.$("#myRequestsTable").fadeOut().promise().done(function(){
                            self.currentlySwitchingFades = false;
                            deps.$("#allRequestsTable").fadeIn();
                            if ( self.lastSwitchTo )
                                self.switchToView(self.lastSwitchTo);
                        });
                    break
                default:
                    deps.$('.pagiBtnHolder').attr('style','');
                    self.requestsMyViewActive = false;
                    self.requestsAllViewActive = false;
                    self.groupsViewActive = true;
                    deps.$("#btnAllRequestView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnMyRequestView").removeClass("btn-primary active").addClass("btn-secondary");
                    deps.$("#btnGroupsView").addClass("btn-primary active").removeClass("btn-secondary");
                    self.currentlySwitchingFades = true;

                    if ( deps.$("#allRequestsTable").is(":visible") )
                        deps.$("#allRequestsTable").fadeOut().promise().done(function(){
                            self.currentlySwitchingFades = false;
                            deps.$("#allMyGroupsTable").fadeIn();
                            if ( self.lastSwitchTo )
                                self.switchToView(self.lastSwitchTo);
                        });
                    else
                        deps.$("#myRequestsTable").fadeOut().promise().done(function(){
                            self.currentlySwitchingFades = false;
                            deps.$("#allMyGroupsTable").fadeIn();
                            if ( self.lastSwitchTo )
                                self.switchToView(self.lastSwitchTo);
                        });
            }
        }

        self.updateGroupName = (groupName) => {
            self.currentNewGroupName =  groupName;

            if ( !groupName)
                deps.$("#btnCreateGroup")[0].disabled = true;
            else
                deps.$("#btnCreateGroup")[0].disabled = false;
        }

        self.checkIfGroupExists = (groupName) => {

            return self.groupNames.includes(groupName.trim());
        }

        self.getAllGroupNamesIfNull = () => {
            
            if( !self.groupNames || !self.groupNames.length )
                Ajax.getGroupNames((data)=> {
                    
                    if ( data !== false)
                        self.groupNames = data;
                })
        }

        var validateGroupName = (name) => {
            var validation = name.match(groupNameRegex);
            if (validation)
                return true;
            return false;
        }

        var setupLecturerPage = () => {
            self.isLecturer = true;
            setupGroupTable();
            deps.$("#btnJoinGroupModalShow").show();
            deps.$("#btnCreateGroupModalShow").show();
            deps.$("#btnCreateGroupModalShow").show();
            deps.$("#btnGroupsView").removeClass("col-6").addClass("col-4");
            deps.$("#btnMyRequestView").removeClass("col-6").addClass("col-4");
            setupMyRequestsTable();

            var setupAllRequestsTable = () => {

                var groupTBody = deps.$("#groupTBodyAllReq");
                groupTBody.html('');
                
                if ( !self.requests.all.length )
                    groupTBody.html(`<div class="text-center noAllRequests"><div>`+
                        ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("noAllRequests"):"Brak oczekujących próśb")
                        +`</div></div>`);
                
                deps.$("#requestsAllCountInfo").text(self.requests.all.length?self.requests.all.length:"");

                for ( let i = 0; i < self.requests.all.length; i++) {
                    var req = self.requests.all[i];
    
                    var newTr = deps.$("<tr>"),
                    indexTd = deps.$("<td>").text(i+1),
                    nameTd = deps.$("<td>").text(req.username),
                    gNameTd = deps.$("<td>").text(req.groupName),
                    infoTd = deps.$("<td>"),
                    acceptTd = deps.$("<td>"),
                    deleteTd = deps.$("<td>");
                    
                    var infoBtn = deps.$(`<button data-toggle="modal" data-target="#recivedRequestGroupModal" aria-hidden="true">`)
                        .text("Info")
                        .addClass(["btn","btn-info","btn-sm"]).on('click',e=>{
                            var target = $(e.target);
                            var id = target.closest("tr")[0].dataset["id"];
                            var name = target.closest("tr")[0].dataset["name"];
                            var user = target.closest("tr")[0].dataset["user"];
                            var creationDate = target.closest("tr")[0].dataset["creationDate"];
                            var type = target.closest("tr")[0].dataset["type"];
                            
                            self.focusedRequestId = id;
                            self.setupRequestInfoModal({
                                name:name,
                                user:user,
                                type:type,
                                creationDate:creationDate
                            });
                        });

                    infoTd.append(infoBtn);

                    var acceptBtn = deps.$(`<button data-toggle="modal" data-target="#acceptGroupModal" aria-hidden="true">`)
                        .text(((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("#groupTBodyAllReq  td:nth-child(5) > button", false, false):"Potwierdź"))
                        .addClass(["btn","btn-success","btn-sm"])
                        .on('click',e=>{
                            var target = $(e.target);
                            var id = target.closest("tr")[0].dataset["id"];
                            var name = target.closest("tr")[0].dataset["name"];
                            var user = target.closest("tr")[0].dataset["user"];
                            deps.$("#acceptGroupHeader").text( user + " " + name);
                            self.focusedRequestId = id;
                        });
                    acceptTd.append(acceptBtn);
                    var declineBtn = deps.$(`<button data-toggle="modal" data-target="#declineGroupModal" aria-hidden="true">`)
                        .text(((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("#groupTBodyAllReq  td:nth-child(6) > button", false, false):"Usuń"))
                        .addClass(["btn","btn-danger","btn-sm"])
                        .on('click',e=>{
                            var target = $(e.target);
                            var id = target.closest("tr")[0].dataset["id"];
                            var name = target.closest("tr")[0].dataset["name"];
                            var user = target.closest("tr")[0].dataset["user"];
                            self.focusedRequestId = id;
                            deps.$("#declineGroupHeader").text( user + " " + name);
                        });
                    deleteTd.append(declineBtn);
    
                    newTr[0].dataset["id"] = req.id;
                    newTr[0].dataset["user"] = req.username;
                    newTr[0].dataset["name"] = req.groupName;
                    newTr[0].dataset["type"] = req.roles[0];
                    newTr[0].dataset["creationDate"] = req.creationDate;
                    
                    newTr.append(indexTd)
                        .append(nameTd)
                        .append(gNameTd)
                        .append(infoTd)
                        .append(acceptTd)
                        .append(deleteTd);
                    groupTBody.append(newTr);
                }
            }
            setupAllRequestsTable();
        }   
        
        self.setupRequestInfoModal = (reqInfo) => {
            
            deps.$("#rrUsername").text(reqInfo.user);
            deps.$("#rrProfile").html(`<a href="/profile/`+reqInfo.user+`" class="playerProfile">
            `+((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("ProfileBtn"):"Profil")+`
            </a>`);
            deps.$("#rrType").text(reqInfo.type);
            deps.$("#rrGroupName").text(reqInfo.name);
            deps.$("#rrDate").text(reqInfo.creationDate);
        }

        var setupPlayerPage = () => {
            self.isLecturer = false;
            setupGroupTable();
            deps.$("#btnCreateGroupModalShow").remove();
            deps.$("#btnAllRequestView").remove();
            deps.$("#btnJoinGroupModalShow").show();
            setupMyRequestsTable();
        }

        var setupMyRequestsTable = () => {

            var groupTBody = deps.$("#groupTBodyMyReq");
            groupTBody.html('');

            if ( !self.requests.my.length )
                groupTBody.html(`<div class="text-center noMyRequests"><div>`+
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("noMyRequests"):"Brak oczekujących próśb")
                    +`</div></div>`);
            
            deps.$("#requestsMyCountInfo").text(self.requests.my.length?self.requests.my.length:"");

            for ( let i = 0; i < self.requests.my.length; i++) {
                var req = self.requests.my[i];

                var newTr = deps.$("<tr>"),
                indexTd = deps.$("<td>").text(i+1),
                nameTd = deps.$("<td>").text(req.groupName),
                codeTd = deps.$("<td>").text(req.groupCode),
                dateTd = deps.$("<td>").text(req.creationDate),
                cancelTd = deps.$("<td>");

                var cancelBtn = deps.$(`<button data-toggle="modal" data-target="#cancelRequestGroupModal" aria-hidden="true">`)
                    .text(((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("CancelRequestBtn"):"Anuluj"))
                    .addClass(["btn","btn-danger","btn-sm"])
                    .on('click',e=>{
                        var target = $(e.target);
                        var code = target.closest("tr")[0].dataset["code"];
                        var name = target.closest("tr")[0].dataset["name"];
                        self.focusedGroupRequest = code;

                        deps.$("#cancelGroupHeader").text( name + " : " + code);
                    });
                cancelTd.append(cancelBtn);

                newTr[0].dataset["code"] = req.groupCode;
                newTr[0].dataset["name"] = req.groupName;

                
                newTr.append(indexTd).append(nameTd).append(codeTd).append(dateTd).append(cancelBtn);
                groupTBody.append(newTr);
            }
        }

        var setupGroupTable = () => {

            var groupTBody = deps.$("#groupTBody");
            groupTBody.html('');

            if ( !self.groups.length )
                groupTBody.html(`<div class="text-center noGroups"><div>`+
                    ((deps.PageLanguageChanger.singleton)?deps.PageLanguageChanger().getTextFor("noGroups"):"Brak grup")
                    +`</div></div>`);
            
            for ( let i = 0; i < self.groups.length; i++) {
                var group = self.groups[i];
                
                var found = false;
                for ( let j = 0; j < self.lobbies.length; j++) {
                    var lobby = self.lobbies[j];
                    if ( lobby.groupName === group.name) {
                        found = true;
                        break;
                    }

                }

                var newTr = deps.$("<tr  "+(found?`class="bg-warning"`:"")+">"),
                indexTd = deps.$("<td>").text(i+1 + (30*(self.pageNum-1))),
                nameTd = deps.$("<td>").text(group.name),
                codeTd = deps.$("<td>").text(group.groupCode),
                dateTd = deps.$("<td>").text(group.creationDate),
                membersTd = deps.$("<td>").text(group.memberCount);

                newTr.on('click',(e)=> {
                    var target = deps.$(e.target);
                    if ( !target.is("tr"))
                        target = target.closest("tr");
                    var groupCode = deps.$(target.children()[2]).text();
                    visitGroup(groupCode);
                })
                
                newTr.append(indexTd).append(nameTd).append(codeTd).append(dateTd).append(membersTd);
                groupTBody.append(newTr);
            }
        }

        var visitGroup = (groupCode) => {
            deps.window.location.replace("/group/" + groupCode);
        }

        /* Other: */
        
        var tooltipsUpdate = () => {
    
            if ( deps.$('[data-toggle="tooltip"]').tooltip !== null && deps.$('[data-toggle="tooltip"]').tooltip !== undefined)
                deps.$('[data-toggle="tooltip"]').tooltip({
                    trigger : 'hover'
                });
        }

        self.resizeWindow = () => {
            if ( self.currentTaskVariant && !self.currentTaskVariant.isTaskDone) {
                deps.$("html").height("100%");
                function isInt(n) {
                    return n % 1 === 0;
                }
                
                var h1 = deps.$(deps.window.document).height();
                if ( isInt (h1))
                    h1 -= 1;
                deps.$("html").height(h1);
                
            } else {
                deps.$("html").height("100%");
    
            }
        }
    
        var displayInfoAnimation = (text, success = true) => {
            var previousMessages = deps.$(".failSuccessInfo");
            previousMessages.each((b,t)=>{
                deps.$(t).css({marginTop: '+=50px'});
            });

            var failInfoDiv = deps.$(`<div class="failSuccessInfo alert alert-`+(success?"success":"danger")+`">` + text + `</div>`)
            deps.$("body").append(failInfoDiv)
        
            failInfoDiv.animate({
              top: "6%",
              opacity: 1
            }, 2000, function() {
              // Animation complete.
              setTimeout(function(){
                failInfoDiv.animate({
                  top: "9%",
                  opacity: 0
                }, 1000, function() {
                  // Second Animation complete.
                  failInfoDiv.remove();
                });
              },2000)
              
            });
        }
        

        /*  initalization  */
        GroupListLogicInit(data);
         
        return self;
    }
    
    GroupListLogic.getInstance = (dataLazy, successfulCreationCallback) => {
    
        if (GroupListLogic.singleton)
            return GroupListLogic.singleton;

        var getCurrentURLPageNum = () => {

            var urlVariantAndTaskset = decodeURI(deps.window.location.href).split("/");
            var urlVariant =  parseInt(urlVariantAndTaskset[urlVariantAndTaskset.length-1]);

            if ( isNaN(urlVariant))
                throw Error("Incorect URL, expected to have a page number!");
            else
                return urlVariant;
        }

        if ( dataLazy )
            return GroupListLogic(dataLazy, successfulCreationCallback);
        else {
            return Promise.all([
                Ajax.getWhoAmI(),
                Ajax.getAccountInfo(),
                Ajax.getGroupsPage(getCurrentURLPageNum()),
                Ajax.getGroupsPage(getCurrentURLPageNum()+1),
                Ajax.getMyRequests(),
                Ajax.getGroupLobbies()
            ]).then((values)=>{

                if ( !values[0] || !values[1] || !values[2] || !values[3] || !values[4] ){
                    if ( successfulCreationCallback )
                        successfulCreationCallback(false);
                    return;
                }
                var playerInfo = values[0];
                var accountInfo = values[1];
                var pageData = {};
                pageData.groups = values[2].content;
                pageData.groupsNext = values[3].content;
                pageData.pageNum = getCurrentURLPageNum();
                var requests = {};
                requests.my = values[4];
                
                
                var data = {
                    account:{
                        playerInfo: playerInfo,
                        accountInfo, accountInfo,
                    },
                    pageInfo: values[2],
                    pageData: pageData,
                    requests: requests,
                    lobbies: values[5]
                }
                
                if ( accountInfo.roles.includes("LECTURER") ) {

                    return Promise.all([
                        Ajax.getAllRequests()
                    ]).then((values)=>{
                        
                        if ( !values[0] ){
                            if ( successfulCreationCallback )
                                successfulCreationCallback(false);
                            return;
                        }

                        requests.all = values[0];
                        GroupListLogic.singleton = GroupListLogic(data, successfulCreationCallback);
                        return GroupListLogic.singleton;
                    }).catch((e) => {
                        console.log('\x1b[31m%s\x1b[0m', 'failed to init 2');
                        console.warn(e);
                        if ( successfulCreationCallback )
                            successfulCreationCallback(false);
                            
                    });
                } else {
                    GroupListLogic.singleton = GroupListLogic(data, successfulCreationCallback);
                    return GroupListLogic.singleton;
                }

                
            }).catch((e) => {
                console.log('\x1b[31m%s\x1b[0m', 'failed to init 1');
                console.warn(e);
                if ( successfulCreationCallback )
                    successfulCreationCallback(false);
                // all requests finished but one or more failed
            });
        }
    }
    
    return {
        GroupListLogic: GroupListLogic,
        getInstance: GroupListLogic.getInstance
    }
})

if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {GroupListModule};