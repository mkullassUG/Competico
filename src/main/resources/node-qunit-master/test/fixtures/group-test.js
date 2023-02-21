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
        type: 'GET',
        url: '/js/lang/group.json',
        contentType: 'application/json',
        dataType: 'json',
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
                "pl": "Wszystkie grupy:",
                "eng": "All groups:"
            },
            "#gameRankingsHyperlink": {
                "pl": "Rankingi",
                "eng": "Rankings"
            },
            "#gameHistoryHyperlink": {
                "pl": "Historia gier",
                "eng": "Game history"
            },
            
        
            
            "OTHER_PAGE_ELEMENTS": {
                "document.title": {
                    "pl": "Grupy",
                    "eng": "Groups"
                }
            }
        }
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
    JSDOM.fromFile("./../templates/group.html").then(domJSDOM => {
                
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

QUnit.module( "Group module", {
    beforeEach: function() {
        //console.log("Setting up DOM beforeEach and mock reset (Group module)");
        return new Promise( newDomMock );
    },

    afterEach: function (e) {
        console.log("afterEach mock reset (Group) "+e.test.module.testsRun+": (" + e.test.testReport.name +")");
        
        if ( $.mockjax.unfiredHandlers().length){
            console.log("unused ajax mocks (Group):");
            console.log($.mockjax.unfiredHandlers());
        }
        if ( $.mockjax.unmockedAjaxCalls().length ) {
            console.log("unmockedAjaxCalls ajax mocks (Group):");
            console.log($.mockjax.unmockedAjaxCalls());
        }

        $.mockjax.clear();
    }
});

/*Uwaga! 
    nodejs niepoinformuje mnie że nie ma jakiejśzmiennej wewnątrz testów, tylko uzna że test się nie powiódł.
*/

test('Group list creation test with lektor and one group', function(assert){
    assert.timeout( 5000 );
    var done = assert.async(); 
    
    mockjax($.merge(lektorMock(),
    []));
    $.mockjaxSettings.logger = null;

    var g = GroupModule({$: $, window: window, NavbarLogic: NavbarLogic, PageLanguageChanger: PageLanguageChanger}).getInstance(false, function(data) {
        assert.equal(1, 1, 'Message after creation try.');
        done();
    });
    
});

