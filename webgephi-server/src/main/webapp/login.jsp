<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Login</title>
</head>
<body>
<h1>Login</h1>
<em></em>


<form name="token-authorization" action="j_security_check" method="POST">

    Username: <input type="text" name="j_username"/>
    <br/>
    Password: <input type="password" name="j_password"/>

    <button type="submit">Click to authorize</button>
</form>
</body>
</html>