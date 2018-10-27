# Bill My Services<br /><span style="font-size: 1em">Paypal Instant Payment Notification</span>

`wsb-paypal-ipn` is a very simple service for _Paypal_ payments processing.

The main goal of `wsb-paypal-ipn` is to recharge some _Bill my services_ `counter` when your customer pay for it using _Paypal_.

## Is `wsb-paypal-ipn` SaaS?

Yes, `wsb-paypal-ipn` is available in the cloud, but the _Paypal IPN_ service require to know your _Paypal_ **IPN** credentials (user and password) for partners (not your _Paypal_ account credentials).

On the other hand, map the payment information (e.g. defined products and buy options) to your specific and customer counters should be, usually, implemented in an _"per use case"_ way.

Anyway, `wsb-paypal-ipn` is a very simple service and you can use it directly on your own servers or fork it for your own purposed.

**When to choose between _SaaS_ and _"on premise"_?**

* If you have an specific billing payment to services mapping and require custom business logic, then **On premise**.
* If you don't want your *IPN* credentials are stored (although encrypted) into <i>Bill My Services</i> servers, then **On premise**.
* If you want a simple way to monetize your applications and services, and trust on <i>Bill My Services</i>, then **SaaS**.

Feel free to contact us: <a href="mailto:bms@computer-mind.com">bms@computer-mind.com</a>

## Custom payment to counter action mapping

You can set a large variety of buying options for your customers. Each specific buy option require one specific `counter` action (reset, create a new one type, increment values, decrement values, update the `counter type` limits, and so on). You should code the `CustomPaymentToCounterMapper` class to fit your own needs.

## Install `wsb-paypal-ipn` on your own servers

### Prerequisites

The only one requirement to run `wsb-paypal-ipn` is: _Java 8_ (or above).

### Installation

1. check you are running _Java 8_ (or above), e.g.
```shell
> java -version
openjdk version "10.0.2" 2018-07-17
OpenJDK Runtime Environment (build 10.0.2+13)
OpenJDK 64-Bit Server VM (build 10.0.2+13, mixed mode)
```

2. the configuration values are:
<table>
<tr><th>System Property Name</th><th>Environment variable name</th><th>Description</th><th>Default</th></tr>
<tr><td>ipn.port</td><td>IPN_PORT</td><td>server listening port</td><td>9192</td></tr>
<tr><td>ipn.mode</td><td>IPN_MODE</td><td>Paypal service mode (sandbox or live)</td><td>sandbox</td></tr>
<tr><td>ipn.username</td><td>IPN_USERNAME</td><td>Paypal seller user name</td><td>(required)</td></tr>
<tr><td>ipn.password</td><td>IPN_PASSWORD</td><td>Paypal seller user password</td><td>(required)</td></tr>
</table>

3. set configuration and run server, you can do it using _Java System Properties_:
```shell
> java \
>     -Dbillmyservices_userid=50 \
>     -Dbillmyservices_secretkey=M6UxiYsELKKHclwYFfKluzvuwj7Bvtk1pY5RUtPhUb4= \
>     -Dipn.username=YOUR_USER_NAME \
>     -Dipn.password=YOUR_USER_PASSWORD \
>     -jar wsb-paypal-ipn.jar
```
or you can do it using _environment variables_:
```shell
> export billmyservices_userid=50
> export billmyservices_secretkey=M6UxiYsELKKHclwYFfKluzvuwj7Bvtk1pY5RUtPhUb4=
> export IPN_USERNAME=YOUR_USER_NAME
> export IPN_PASSWORD=YOUR_USER_PASSWORD
> java -jar wsb-paypal-ipn.jar
```

**Remember** replace `billmyservices` values to your own <i>Bill My Services</i> account.

## Activating Paypal IPN

After setup your server, configure _Paypal IPN_ service.

https://developer.paypal.com/docs/classic/ipn/integration-guide/IPNSetup/#setting-up-ipn-notifications-on-paypal

