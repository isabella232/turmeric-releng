xquery version "1.0";
(:
ASSERTION RULE - WDL Operations must have one input and one output
:)

declare boundary-space preserve;
declare copy-namespaces no-preserve, inherit;

declare namespace wsdl="http://schemas.xmlsoap.org/wsdl/";
declare namespace xs="http://www.w3.org/2001/XMLSchema";

(: import module namespace rmassert="http://ebay.com/repository/assertion" at "rmassert_corelib.xq"; :)

declare variable $assertionName := "WSDLPortTypeOperations";
declare variable $failureTemplate := 'Operation "##" must have only one input and one output';
declare variable $FILE_PATH as document-node() external;

declare function local:fmtAssertionMessage($messageTemplate as xs:string, $errData as xs:string*) as element()
{
    let $segments := fn:tokenize($messageTemplate, "##")
    let $cntSeg := fn:count($segments)
    let $cntErr := fn:count($errData)
    let $message := fn:string-join(
        for $i in (1 to $cntSeg)
            return ($segments[$i],
                if (fn:exists($errData[$i])) then $errData[$i]
                    else if ($i lt $cntSeg) then ("?") else ()), '')
    return <assertionMessage>{$message}</assertionMessage>
};

let $doc := $FILE_PATH
return
<assertion name="{$assertionName}">
{
let $elementAssertion := 
	for $element in $doc//wsdl:portType/wsdl:operation
	return 
		if ((fn:count($element/wsdl:input) != 1) or (fn:count($element/wsdl:output) != 1))
		then local:fmtAssertionMessage($failureTemplate,($element/@name))
	    else ()
	    
return ($elementAssertion)
}</assertion>