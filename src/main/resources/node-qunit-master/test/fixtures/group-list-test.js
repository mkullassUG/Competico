const jsdom = require("jsdom");
const { JSDOM } = jsdom;
const mockjaxFunc = require("jquery-mockjax");
var mockjax, $, window, dom;

// var cytoscape; // wersja 3.18.2 jak u klienta
/*
    lepszą opcją jest mokowanie cytoscope jakimś innym obiektem bo ten moduł i trzeba zamieniacsporo rzeczy w kodzie
*/

/*Uwaga!
    jeśli zmienie nazwe template.html to nie wiem co się stanie bez reject_ w JSDOM dom

*/
var playerMock = () => [
    {
        url: '/api/v1/playerinfo',
        responseText: { 
            "username": "mockUsername",
            "nickname": "mockNickname",
            "isHost": false,
            "gameStarted": false,
            "gameCode": "mockGameCode"
        },
        type: "GET"
    },
    {
        url: '/api/v1/account/info',
        responseText: { 
            "authenticated": true,
            "email": "mockemail@cos.com",
            "emailVerified": false,
            "username": "mockUsername",
            "nickname": "mockNickname",
            "roles": ["PLAYER"]
        },
        type: "GET"
    },
];
var pageTranslationMock = () => [
    {
        url: '/js/lang/group-list.json',
        responseText: {
            "#taskSettingsPanel":{
                "pl": "Zarządzanie zestawami",
                "eng" : "Taskset management"
            },
            "#btnListWordFill": {
                "pl": "Wybierz słowo",
                "eng" : "Choose word"
            },
            "#btnListChoiceWordFill": {
                "pl": "Wybierz właściwą opcję",
                "eng" : "Choose correct option"
            },
            "#twoColumnsWariant": {
                "pl": "Połącz w pary",
                "eng" : "Pair up"
            },
            "#btnChronologicalOrder": {
                "pl": "Porządkowanie chronologiczne",
                "eng" : "Chronological ordering"
            },
            "#btnListSentenceForming": {
                "pl": "Układanie zdań z podanych wyrazów",
                "eng" : "Arranging sentences"
            },
            "#picturesVariant": {
                "pl": "Warianty z obrazkami",
                "eng" : "Variants with pictures"
            },
            "#btnWordConnect": {
                "pl": "Łączenie linią",
                "eng" : "Connect line"
            },
            "#btnWordConnectAnswerType": {
                "pl": "Dopasowanie drugiej kolumny przez ręczne wpisywanie odpowedzi",
                "eng" : "Match the second column by typing"
            },
            "#btnWordConnectAnswerPool": {
                "pl": "Dopasowanie drugiej kolumny z podanego zestawu",
                "eng" : "Match the second column from set"
            },
            "#btnPictureToManyWords": {
                "pl": "Jeden obrazek i wiele odpowiedzi.",
                "eng" : "One picture and many answers"
            },
            "#btnSentenceToManyPictures": {
                "pl": "Jeden tekst i wiele obrazków",
                "eng" : "One text and many pictures"
            },
            "#btnManyPicturesManyWords": {
                "pl": "Wiele obrazków i wiele słów",
                "eng" : "Lots of pictures and lots of words"
            },
        
            
        
            ".footer > .container.text-center > .footer-text.m-auto": {
                "pl": "Ucz się angielskiego razem z nami! <br> © 2020-2021 Artur Lech, Michał Kullass.",
                "eng" : "Let's learn english together! <br> © 2020-2021 Artur Lech, Michał Kullass."
            },
            ".footer > .container.text-center> .text-muted.m-auto": {
                "pl": "Ucz się angielskiego razem z nami! <br> © 2020-2021 Artur Lech, Michał Kullass.",
                "eng" : "Let's learn english together! <br> © 2020-2021 Artur Lech, Michał Kullass."
            },
            "#homepageHyperlink > a": {
                "pl": "Strona Domowa",
                "eng": "Home Page"
            },
            "#gameHyperlink > a": {
                "pl": "Gra",
                "eng": "Game"
            },
            "#helpHyperlink > a": {
                "pl": "Wsparcie",
                "eng": "Support"
            },
            "#registerHyperlink > a": {
                "pl": "Rejestracja",
                "eng": "Register"
            },
            "#loginHyperlink": {
                "pl": "Zaloguj",
                "eng": "Login"
            },
            "#dashboardHyperlink > a": {
                "pl": "Rankingi",
                "eng": "Dashboard"
            },
            "#profileHyperlink > a": {
                "pl": "Profil",
                "eng": "Profile"
            },
            "#logOutButton": {
                "pl": "Wyloguj",
                "eng": "Logout"
            },
            "#groupsHyperlinkMainNav > a": {
                "pl": "Grupy",
                "eng": "Groups"
            },
            "#allGroupsNames": {
                "pl": "Moje grupy",
                "eng": "My groups"
            },
            "#gameRankingsHyperlink": {
                "pl": "Rankingi",
                "eng": "Rankings"
            },
            "#gameHistoryHyperlink": {
                "pl": "Historia gier",
                "eng": "Game history"
            },
            
            ".pageNumText": {
                "pl": "Strona: ",
                "eng": "Page: "
            },
            "#groupTableDiv > div.col-12.d-flex.justify-content-around > h5": {
                "pl": "Grupy: ",
                "eng": "Groups: "
            },
            "#allMyGroupsTable > thead > tr > th:nth-child(2)":{
                "pl": "Nazwa",
                "eng": "Name"
            },
            "#allMyGroupsTable > thead > tr > th:nth-child(3)":{
                "pl": "Kod",
                "eng": "Code"
            },
            "#allMyGroupsTable > thead > tr > th:nth-child(4)":{
                "pl": "Data",
                "eng": "Date"
            },
            "#allMyGroupsTable > thead > tr > th:nth-child(5)":{
                "pl": "Członkowie",
                "eng": "Members"
            },
            "#btnAddGroupModalShow": {
                "pl": "Dołącz do grupy",
                "eng": "Join group"
            },
            "#btnCreateGroupModalShow": {
                "pl": "Stwórz grupę",
                "eng": "Create group"
            },
            "#CreateGroupModalLongTitle":{
                "pl": "Nowa grupa",
                "eng": "New group"
            },
            "#newGroupHeader":{
                "pl": "Wprowadź nazwe grupy",
                "eng": "Provide group name"
            },
            "#newGroupInputLabel":{
                "pl": "Nazwa",
                "eng": "Name"
            },
            "#invalidGroupInfoBad": {
                "pl": "Wprowadź poprawą nazwe group.",
                "eng": "Provide correct group name."
            },
            "#invalidGroupInfoExists":{
                "pl": "Grupa o tej nazwie istnieje.",
                "eng": "Group with given name exists."
            },
            "#nextPageNamebtn": {
                "pl": "następna →",
                "eng": "next → "
            },
            "#prevPageNamebtn":{
                "pl": " ← poprzednia",
                "eng": " ← prev"
            },
            ".cancelBtn": {
                "pl": "Anuluj",
                "eng": "Cancel"
            },
            "#btnCreateGroup": {
                "pl": "Stwórz",
                "eng": "Create"
            },
            "#btnGroupsView h5": {
                "pl": "Grupy",
                "eng": "Groups"
            },
            "#btnRequestView h5": {
                "pl": "Prośby",
                "eng": "Requests"
            },
        
        
            "OTHER_PAGE_ELEMENTS": {
                "document.title": {
                    "pl": "Grupy",
                    "eng": "Groups"
                },
                "noGroups": {
                    "pl": "Brak grup",
                    "eng": "No groups"
                },
                "noRequests": {
                    "pl": "Brak próśb",
                    "eng": "No requests"
                }
            }
        },
        type: "GET"
    }
];
var lektorMock = () =>[
    {
        url: '/api/v1/playerinfo',
        responseText: { 
            "username": "mockUsername",
            "nickname": "mockNickname",
            "isHost": true,
            "gameStarted": false,
            "gameCode": "mockGameCode"
        },
        type: "GET"
    },
    {
        url: '/api/v1/account/info',
        responseText: { 
            "authenticated": true,
            "email": "mockemail@cos.com",
            "emailVerified": false,
            "username": "mockUsername",
            "nickname": "mockNickname",
            "roles": ["LECTURER"]
        },
        type: "GET"
    },
];
function newDomMock( resolve, reject_ ) {
    JSDOM.fromFile("./../templates/group-list.html").then(domJSDOM => {
                
        //console.log("done Setting up group-list DOM");
        dom = domJSDOM;
        window = dom.window;
        dom.reconfigure({  url: "http://mockAddress/groups/1"});
        window = Object.assign(window, { innerWidth: 500 });
        Object.defineProperty(window, 'innerWidth', {writable: true, configurable: true, value: 200})
        $ = require('jquery')(window);
        mockjax = mockjaxFunc($, window);
        resolve(dom);
    }).catch((e)=>{
        console.warn("ERROR COULD NOT FIND HTML TEMPLATE!!!!!!!!!!!!!!!!");
        resolve(false);
    });
}

QUnit.module( "Group list module", {
    beforeEach: function() {
        //console.log("Setting up DOM beforeEach and mock reset (Group list module)");
        return new Promise( newDomMock );
    },

    afterEach: function (e) {
        // console.log("afterEach mock reset (Group list) "+e.test.module.testsRun+": (" + e.test.testReport.name +")");
        
        if ( $.mockjax.unfiredHandlers().length){
            console.log("unused ajax mocks (Group list):");
            console.log($.mockjax.unfiredHandlers());
        }
        if ( $.mockjax.unmockedAjaxCalls().length ) {
            console.log("unmockedAjaxCalls ajax mocks (Group list):");
            console.log($.mockjax.unmockedAjaxCalls());
        }
        // console.log("mockedAjaxCalls ajax mocks:");
        // console.log($.mockjax.mockedAjaxCalls())
        $.mockjax.clear();
    }
});

/*Uwaga! 
    nodejs niepoinformuje mnie że nie ma jakiejśzmiennej wewnątrz testów, tylko uzna że test się nie powiódł.
*/

test('Group list creation test with lektor and one group', function(assert){
    assert.timeout( 5000 );
    var done = assert.async(); 
    
    var requestMocks = $.merge(
        $.merge(
            lektorMock(),
            [
                { 
                    url: '/api/v1/groups/1',
                    responseText: {
                        content:
                        [ 
                            {
                                "name": "nameMock",
                                "groupCode": "groupCodeMock",
                                "creationDate": "creationDateMock",
                                "memberCount": 1
                            }
                        ]
                    },
                    type: "GET"
                },
                {   
                    url: '/api/v1/groups/2',
                    responseText: {
                        content:
                        [ ]
                    },
                    type: "GET"
                },
                {
                    type: 'GET',
                    url: '/api/v1/groups/requests/my',
                    responseText: [],
                },
                {
                    type: 'GET',
                    url: '/api/v1/groups/requests/all',
                    responseText: [],
                },
                {
                    type: 'GET',
                    url: '/api/v1/groups/lobbies',
                    responseText: [],
                }
            ]
            ),pageTranslationMock()
        );
    mockjax(requestMocks);

    $.mockjaxSettings.logger = null;

    var gl = GroupListModule({$: $, window: window, NavbarLogic: NavbarLogic, PageLanguageChanger: PageLanguageChanger}).getInstance(false, function(data) {
        
        assert.equal(typeof data === 'object', true, 'Successfully recived message from Group list Init function.');
        assert.equal(data.pageNum, 1, 'Page number is equal to 1.');
        assert.equal(data.groups.length, 1, 'Number of groups is equal to 1".');
        assert.equal(data.playerInfo.accountInfo.username, "mockUsername", 'Username is equal to mockUsername".');
        assert.equal(data.isLecturer, true, 'Page setup for lcturer".');

        done();
    });
    
});

test('Group list creation test init exceptions', function(assert){
    assert.timeout( 5000 );
    var done = assert.async(); 
    
    $.mockjaxSettings.logger = null;
    
    assert.throws(
        ()=>{
            GroupListModule({window: window, NavbarLogic: NavbarLogic, PageLanguageChanger: PageLanguageChanger})
                .getInstance(false, function(data) {})
        },
        Error("jQuery not defined"),
        "No $ module exception"
    );
    
    assert.throws(
        ()=>{
            GroupListModule({$: $, NavbarLogic: NavbarLogic, PageLanguageChanger: PageLanguageChanger})
                .getInstance(false, function(data) {})
        },
        Error("window not defined"),
        "No window module exception"
    );

    setTimeout(function(){
        done();
    },1000)
});

test('Group list create new group test', function(assert){
    assert.timeout( 5000 );
    var done = assert.async(); 
    
    var requestMocks = $.merge(
        // $.merge(
        lektorMock(),
    [
        {
            url: '/api/v1/groups/1',
            responseText: 
            {   
                content: 
                [ 
                    {
                        "name": "nameMock",
                        "groupCode": "groupCodeMock",
                        "creationDate": "creationDateMock",
                        "memberCount": 1
                    }
                ]
            },
            type: "GET"
        },
        {
            url: '/api/v1/groups/2',
            responseText: {
                content: [ ]      
            },
            type: "GET"
        },
        {
            url: '/api/v1/groups',
            responseText: "groupCodeMock2",
            type: "POST"
        },
        {
            type: 'GET',
            url: '/api/v1/groups/requests/my',
            responseText: {},
        },
        {
            type: 'GET',
            url: '/api/v1/groups/requests/all',
            responseText: {},
        },
        {
            type: 'GET',
            url: '/api/v1/groups/lobbies',
            responseText: [],
        }
    ]
    // ),pageTranslationMock()
    );
    mockjax(requestMocks);
    $.mockjaxSettings.logger = null;

    var gl = GroupListModule({$: $, window: window, NavbarLogic: NavbarLogic, PageLanguageChanger: PageLanguageChanger}).getInstance(false, function(data) {
        
        data.createNewGroup("myNewGroup");

        setTimeout(function(){
            assert.equal(data.groups.length, 2, 'Expected 2 groups to exist".');
            done();
        },500)
        
    });
});

test('Group list new group input test', function(assert){
    assert.timeout( 5000 );
    var done = assert.async(); 
    
    var requestMocks = $.merge(
        // $.merge(
            lektorMock(),
    [
        {
            url: '/api/v1/groups/1',
            responseText: 
            {  
                content: 
                [ 
                    {
                        "name": "nameMock",
                        "groupCode": "groupCodeMock",
                        "creationDate": "creationDateMock",
                        "memberCount": 1
                    }
                ]
            },
            type: "GET"
        },
        {
            url: '/api/v1/groups/2',
            responseText: {
                content: [ ]      
            },
            type: "GET"
        },
        {
            url: '/api/v1/groups/names',
            responseText: ["nameMock"],
            type: "GET"
        },
        {
            type: 'GET',
            url: '/api/v1/groups/requests/my',
            responseText: {},
        },
        {
            type: 'GET',
            url: '/api/v1/groups/requests/all',
            responseText: {},
        },
        {
            type: 'GET',
            url: '/api/v1/groups/lobbies',
            responseText: [],
        }
    ]
    // ),pageTranslationMock()
    );
    mockjax(requestMocks);

    $.mockjaxSettings.logger = null;
    
    var gl = GroupListModule({$: $, window: window, NavbarLogic: NavbarLogic, PageLanguageChanger: PageLanguageChanger}).getInstance(false, function(data) {
        
        $("#btnCreateGroupModalShow").click();
        // data.groupNames = ["nameMock"];
        setTimeout(function(){

            // if ( !data.groupNames.length )
            //     data.groupNames = ["nameMock"];
            $("#newGroupInput").val("newTypedGroupName");
            $('#newGroupInput').click();

            setTimeout(function(){

                assert.equal(data.currentNewGroupName, "newTypedGroupName", 'Expected value to be "newTypedGroupName" from html input tag.');
                done();
            },500)
        },500)
        
    });
});