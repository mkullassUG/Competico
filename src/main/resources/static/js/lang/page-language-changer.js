/*
tekst statyczny,
tekst statyczny się przez kod dynamicznie,
tekst zmianiający się przez kod dynamicznie,
teskt wstawiant przez kod tylko raz.
(na przycisk zmiany języka podzial lub dynamicznie samo)
*/
const PageLanguageChanger = (data, debug = false, depMocks = {}, successfulCreationCallback) => {
  
    /*  singleton   */
    if (PageLanguageChanger.singleton)
        return PageLanguageChanger.singleton;
    var self = {};
    if (!PageLanguageChanger.singleton && data)
        PageLanguageChanger.singleton = self;
    else if (!PageLanguageChanger.singleton && !data)
        return PageLanguageChanger.getInstance(false, debug, depMocks, successfulCreationCallback);

    /*  environment preparation  */
    $ = typeof $ != 'undefined'? $ : depMocks.$mock;
    window =  typeof window != 'undefined'? window : depMocks.windowMock;

    /*       logic variables          */
    self.pageName;
    self.langString;
    self.jsonData;
    /*       logic functions          */
    var PageLanguageChangerInit = (langData) => {
        
        self.pageName = langData.pageName;
        self.langString = langData.langString;
        self.jsonData = langData.jsonData;

        if ( !localStorage.lang  || localStorage.lang == "pl") {
            localStorage.lang = "pl";
            $("#languageSelected").html(`<img alt="🇵🇱" class="imga" style="width: 16px;" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAMAAABiM0N1AAACxFBMVEUAAAD/AAD///+2JCS/ICDhJC/cIizVFSvbHyrt7e3////UHCXgJC/fJC7aICnv7+/z8/PRFy7VHCbfICvlIyz09PTv7+/y8vLeIyz/AADz8/P19fXWHCfVGiLVHSbhJTDVHCfXHSfbJC3dIi/bHCjt7e3VHCb39/f09PTZHSnYISzu7u7v7+/VHCvjJS/cIivcIyzZICrWHCbx8fHfIy309PTYICfZHyj09PT////XIij29vby8vLy8vLaHyv29vbx8fHdIi309PTu7u7z8/Pz8/Pz8/Py8vLx8fHv7+/jJi/z8/PeISnXHin39/fu7u7bHSjWICbXHij19fXx8fHx8fHYHyny8vLv7+/v7+/jJi/09PTz8/P09PT19fXcIiz29vbjJjDjJS/x8fHhJSzgIy/hJy7aICrZHynjIy7hJTD39/fZHirbICriJS/gJS/hJC/fJjDWHCfkJTD09PTXHSjfJC3cIy719fXZICrdIi3fIy/jJi/iJTD19fXXHijVKyvdIiv19fX////19fXx8fH19fXXHinVHCX29vb09PT19fXdISzZHiv////29vb09PTcISzkJC7bISreIizz8/PgJC/y8vLZHCb09PT19fX09PT39/ffJCzaICrUHCb39/fYHif09PT09PTbICj09PTVHSbZHyn29vb29vb19fX29vb29vb09PTXHij19fXZHyn29vb19fX19fX39/f4+PjXHijbICr09PTWHiXXHyjVHSfWHSf29vb19fXx8fH19fXVHCb29vb////RFyP19fXWHSX29vb29vb29vb29vb19fXOGCT29vbVHCf09PTUGyb19fXw8PD39/f19fX29vb09PTx8fHz8/PfJC7cIizaICrw8PDZHyneIy3dIy3XHijv7+/gJC7WHSfVHSfUHCbhJS/y8vLjJjD19fX29vb09PRBLbWNAAAA13RSTlMAAwMHCKL9DDFGBL+U+va9+wv6GB39MeRzAuytox6w5aXqOTw/HbYec1guHh82bcTs8WST5XRvxBcNLXPn5opyuNH2D/oVZmGncRvFH9KDSUZx/pORpMN4sa+9W1bw9ur7r6SibpJw5uRIRR+G5eOno2ZjYBhZtxa37VtXvLGD5QZ47QzsberMbh3R/dA8BTmKhRzB58Sz5Rsu5/E9QO+UPzvt70At5OgcxM/DG4b8/o42zNI8cevus29s5X1ZtqPltVgHFmSm5W3kpWMVokhvlea8vZRuRiejyDoAAAKjSURBVHhe1NQ7ysJAFIZhSWkfmyzAwloQ/kLQUrCxVtLbWvyVhYqggrGx8FIERfCKqLFUgkE0kSCSmD3MzCasEyfJzHQ+C3jhwMeJ/Jiy87ZNwbIEs3B34oyRytW4QRfdeKRoK9rpX4QY4vmiUWTqaRX6alazhJnoXw0Gahw5ks5uD0MdlNAMl+8AAlu7G9xpLQCh1TyoM9MBsWHMvyNNAIWi7NeRS4BKZozvDKaI0gh7XeKFqKlrzAxziEH/e5o9xGTj7Sg8YMJLnkG3AaOl+7gkYvZ0/Y0PafWKwyAQQFEUiwLXZDwJAlfVpE0q+CoMhF8dW+gWWANbqUPMDjCMqKmabTR5mQxo3l3AsVeo04nbAWoUUbg78s5AD2mhQVG9LPTUVB/7C5+D4sRAqSbLDJSz0GKgmYUuDoo2ugBQyUMuoIKHJkBXHqoA1V+6EVDLQx0g70e3Aur/pMy7DUBQGAXgv6BzK3obqIxhCRISCo9ISDwSdhC7aRQKMYbkuO4C5xvge2gTopmPYkQJH9mIHD5KEWU3LUCU81GBKOIjT8Bnn1I+FRvVOmouUqsjVx0U1YvWcdEuv4GLRhNZ20kIFzFWJnqrmX+XBKIADuDfR4cdHBwoDSfcHVoRGIg/BoOGoFUQtMG1RbBBl6ChqbEGG6zFxSIQ3cTJUFsCcRJxVkSh+j8cvbvunu+9zc8f8NGwsR9fCrv2w0ITjwqwIpczQRUCmyOf2OM7hMOFWFSFU/B1LiBG8M9Bkf8pp+Hi9JP3SchwVVcXXNRjePi453neGvB0fsP+tGRQvJytGOVroCKlNktz1SHYJvq4/ck8gwHJdX+owqkg2Ej9B0rz5Qc76e4k8Osi8B3ZA6dhcjT4s2mGbscQY0x6U91UFFN/f8oa2C1rNdZA8W7uzHUAAAAASUVORK5CYII=">`);
            $("#languageOption2").removeClass(`bg-light`);
            $("#languageOption1").addClass("bg-light");

        } else if ( localStorage.lang == "eng") {
            
            $("#languageSelected").html(`<img alt="🇬🇧" class="imga" style="width: 16px;"   src="data:image/png;base64,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">`);
            $("#languageOption1").removeClass("bg-light");
            $("#languageOption2").addClass("bg-light");
        }

        setupPageLanguage(self.langString, self.pageName, self.jsonData );

        listenersSetup();

        if ( successfulCreationCallback )
            successfulCreationCallback(true);
    }
    
    /*      listeners      */
    var listenersSetup = () => {
        if ($("#languageOption1").length) {

            $("#languageOption1").on('click',() => {
                self.languageChanged();
                $("#languageSelected").html(`<img alt="🇵🇱" class="imga" style="width: 16px;" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAMAAABiM0N1AAACxFBMVEUAAAD/AAD///+2JCS/ICDhJC/cIizVFSvbHyrt7e3////UHCXgJC/fJC7aICnv7+/z8/PRFy7VHCbfICvlIyz09PTv7+/y8vLeIyz/AADz8/P19fXWHCfVGiLVHSbhJTDVHCfXHSfbJC3dIi/bHCjt7e3VHCb39/f09PTZHSnYISzu7u7v7+/VHCvjJS/cIivcIyzZICrWHCbx8fHfIy309PTYICfZHyj09PT////XIij29vby8vLy8vLaHyv29vbx8fHdIi309PTu7u7z8/Pz8/Pz8/Py8vLx8fHv7+/jJi/z8/PeISnXHin39/fu7u7bHSjWICbXHij19fXx8fHx8fHYHyny8vLv7+/v7+/jJi/09PTz8/P09PT19fXcIiz29vbjJjDjJS/x8fHhJSzgIy/hJy7aICrZHynjIy7hJTD39/fZHirbICriJS/gJS/hJC/fJjDWHCfkJTD09PTXHSjfJC3cIy719fXZICrdIi3fIy/jJi/iJTD19fXXHijVKyvdIiv19fX////19fXx8fH19fXXHinVHCX29vb09PT19fXdISzZHiv////29vb09PTcISzkJC7bISreIizz8/PgJC/y8vLZHCb09PT19fX09PT39/ffJCzaICrUHCb39/fYHif09PT09PTbICj09PTVHSbZHyn29vb29vb19fX29vb29vb09PTXHij19fXZHyn29vb19fX19fX39/f4+PjXHijbICr09PTWHiXXHyjVHSfWHSf29vb19fXx8fH19fXVHCb29vb////RFyP19fXWHSX29vb29vb29vb29vb19fXOGCT29vbVHCf09PTUGyb19fXw8PD39/f19fX29vb09PTx8fHz8/PfJC7cIizaICrw8PDZHyneIy3dIy3XHijv7+/gJC7WHSfVHSfUHCbhJS/y8vLjJjD19fX29vb09PRBLbWNAAAA13RSTlMAAwMHCKL9DDFGBL+U+va9+wv6GB39MeRzAuytox6w5aXqOTw/HbYec1guHh82bcTs8WST5XRvxBcNLXPn5opyuNH2D/oVZmGncRvFH9KDSUZx/pORpMN4sa+9W1bw9ur7r6SibpJw5uRIRR+G5eOno2ZjYBhZtxa37VtXvLGD5QZ47QzsberMbh3R/dA8BTmKhRzB58Sz5Rsu5/E9QO+UPzvt70At5OgcxM/DG4b8/o42zNI8cevus29s5X1ZtqPltVgHFmSm5W3kpWMVokhvlea8vZRuRiejyDoAAAKjSURBVHhe1NQ7ysJAFIZhSWkfmyzAwloQ/kLQUrCxVtLbWvyVhYqggrGx8FIERfCKqLFUgkE0kSCSmD3MzCasEyfJzHQ+C3jhwMeJ/Jiy87ZNwbIEs3B34oyRytW4QRfdeKRoK9rpX4QY4vmiUWTqaRX6alazhJnoXw0Gahw5ks5uD0MdlNAMl+8AAlu7G9xpLQCh1TyoM9MBsWHMvyNNAIWi7NeRS4BKZozvDKaI0gh7XeKFqKlrzAxziEH/e5o9xGTj7Sg8YMJLnkG3AaOl+7gkYvZ0/Y0PafWKwyAQQFEUiwLXZDwJAlfVpE0q+CoMhF8dW+gWWANbqUPMDjCMqKmabTR5mQxo3l3AsVeo04nbAWoUUbg78s5AD2mhQVG9LPTUVB/7C5+D4sRAqSbLDJSz0GKgmYUuDoo2ugBQyUMuoIKHJkBXHqoA1V+6EVDLQx0g70e3Aur/pMy7DUBQGAXgv6BzK3obqIxhCRISCo9ISDwSdhC7aRQKMYbkuO4C5xvge2gTopmPYkQJH9mIHD5KEWU3LUCU81GBKOIjT8Bnn1I+FRvVOmouUqsjVx0U1YvWcdEuv4GLRhNZ20kIFzFWJnqrmX+XBKIADuDfR4cdHBwoDSfcHVoRGIg/BoOGoFUQtMG1RbBBl6ChqbEGG6zFxSIQ3cTJUFsCcRJxVkSh+j8cvbvunu+9zc8f8NGwsR9fCrv2w0ITjwqwIpczQRUCmyOf2OM7hMOFWFSFU/B1LiBG8M9Bkf8pp+Hi9JP3SchwVVcXXNRjePi453neGvB0fsP+tGRQvJytGOVroCKlNktz1SHYJvq4/ck8gwHJdX+owqkg2Ej9B0rz5Qc76e4k8Osi8B3ZA6dhcjT4s2mGbscQY0x6U91UFFN/f8oa2C1rNdZA8W7uzHUAAAAASUVORK5CYII=">`);
                localStorage.lang = "pl";
                $("#languageOption2").removeClass("bg-light");
                $("#languageOption1").addClass("bg-light");
                self.languageChanged();
            });
        }
    
        if ($("#languageOption2").length) {

            $("#languageOption2").on('click',() => {
                $("#languageSelected").html(`<img alt="🇬🇧" class="imga" style="width: 16px;"   src="data:image/png;base64,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">`);
                localStorage.lang = "eng";
                $("#languageOption1").removeClass("bg-light");
                $("#languageOption2").addClass("bg-light");
                self.languageChanged();
            });
        }
    }
    
    self.getTextFor = (selector, language, isOther = true) => {

        if ( !isOther ) 
            return self.jsonData[selector][(language? language:self.langString)];
        else if ( selector != 'OTHER_PAGE_ELEMENTS') 
            return self.jsonData.OTHER_PAGE_ELEMENTS[selector][(language? language:self.langString)];
    }

    var setupPageLanguage = (langString, pageName, jsonData , languageText) => {

        var otherElements;
        if ( jsonData.OTHER_PAGE_ELEMENTS )
            otherElements = jsonData.OTHER_PAGE_ELEMENTS;

        var ObjectsToChange = Object.keys(jsonData);
        ObjectsToChange.splice(ObjectsToChange.indexOf('OTHER_PAGE_ELEMENTS'), 1);

        if ( successfulCreationCallback )
            successfulCreationCallback(jsonData, langString);

        $("#languageSelected").html(languageText);

        for (let i = 0; i < ObjectsToChange.length; i++) {
            var selector = ObjectsToChange[i];
            findBySelectorAndChangeTo(selector, jsonData[selector][langString], jsonData[selector].ATTRIBUTE, jsonData[selector].NOT);
        }

        var otherElementsKeys = Object.keys(otherElements);
        for ( let i = 0; i < otherElementsKeys.length; i++) {
            var oElemKey = otherElementsKeys[i];
            var oElem = otherElements[oElemKey];

            if ( oElemKey == 'document.title')
                window.document.title = oElem[langString];
        }

    }

    var findBySelectorAndChangeTo = (selector, text, attr, not = "") => { 
        if ( $(selector).length )
            if ( attr )
                $(selector).not(not).attr(attr,text);
            else
                $(selector).not(not).html(text);
    }

    self.languageChanged = () => {

        setupPageLanguage(
            localStorage.lang, 
            self.pageName, 
            self.jsonData
        );
    }
    
    /*     ajax http actions       */
    
    /*   ajax http requests       */
  
    /*  initalization  */
    PageLanguageChangerInit(data);
    
    return self;
}

PageLanguageChanger.OTHER_PAGE_ELEMENTS = {

}

PageLanguageChanger.getInstance = (dataLazy, debug = false, depMocks = {}, successfulCreationCallback) => {
    
    if (PageLanguageChanger.singleton)
      return PageLanguageChanger.singleton;

    $ = typeof $ != 'undefined'? $ : depMocks.$mock;
    window = typeof window != 'undefined'? window : depMocks.windowMock;

    if ( !localStorage.lang )
        localStorage.lang = "pl";

    var getAndSetupLanguage = () => {
    
        var path = window.location.pathname.split("/");
        var pageName = path[path.length-1];

        function containsAll(needles, haystack){ 
            for(var i = 0; i < needles.length; i++){
                if($.inArray(needles[i], haystack) == -1) return false;
            }
            return true;
        }

        //jak to zrobić w grze i lobby gdzie nie ma przycisku wyboru języka?
        if ( containsAll(["game", "history"],path) )
            pageName = "game-history";
        else if ( "profile" === path[1]) {
            pageName = "profile";
        } else if ( containsAll(["tasks", "import", "global"],path) || containsAll(["lecturer", "taskmanager"],path)) {
            pageName = "taskManager";
        } else  if ( containsAll(["resetpassword"],path))
            pageName = "resetpassword";
        else if ( pageName == "") 
            //TODO: TO NIE ZAWSZE BĘDZIE DZIAŁAĆ, na w grze bo ostatne jest kodem gry nie nazwą strony, poprawić jak wyżej
            pageName = "index";
        
        ajaxReceiveLangInfo(
            localStorage.lang,
            pageName
        );
    }

    var ajaxReceiveLangInfo = ( langString, pageName ) => {

        $.ajax({
            type     : "GET",
            cache    : false,
            url      : "/js/lang/" + pageName + ".json",
            contentType: "application/json",
            dataType: 'json',
            success: function(jsonData, textStatus, jqXHR) {
                if (debug){
                    console.warn("ajaxReceiveLangInfo success");
                    console.warn(debug);
                }
                
                var data = {
                    pageName: pageName,
                    langString: langString,
                    jsonData: jsonData
                };

                PageLanguageChanger(data, debug, depMocks, successfulCreationCallback);
            },
            error: function(jqXHR, status, err) {
                if (debug){
                    console.warn("ajaxReceiveLangInfo error");
                    console.warn(jqXHR);
                }
            }
        });
    }

    if ( dataLazy )
        PageLanguageChanger.singleton = PageLanguageChanger(dataLazy, debug, depMocks);
    else
        getAndSetupLanguage();
    
    return PageLanguageChanger.singleton;
}


if (typeof module !== 'undefined' && typeof module.exports !== 'undefined')
    module.exports = {PageLanguageChanger};