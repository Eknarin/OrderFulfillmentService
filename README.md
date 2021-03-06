# Order Fulfillment Service
 Our service is made for receiving orders which come from the e-commerce, keep those order, wait to be fulfilled and also track those order until they are fulfilled or approved and the e-commerce can check their orders' status. 
 This service requires other services to succeed its goal which is Shipment Service.

## What is Order Fulfillment ?
* Order fulfillment is the complete process from point of sales inquiry to delivery of a product to the customer. Sometimes Order Fulfillment is used to simply describe the act of distribution (logistics) or the shipping function, but in the broader sense it refers to the way firms respond to customer orders, and the process they take to move products from those orders, to the customer.
* In simpler terms, Order Fulfillment is everything that a seller does from the moment an order for a product is received, to the customer having their purchase in their hands. Order Fulfillment also includes the processes involved in receiving products to sell, storing those products, and providing inventory control of those products.
* From http://www.fulfillmentwarehouse.biz/what-is-fulfillment.asp

## Demo
* Service : http://128.199.175.223:8000/fulfillment/orders
* Web-Admin : http://128.199.175.223/fulfillment<br>
(Username : admin, Password : 12341234)

## Stakeholder
* E-Commerce
* Order Fulfiller

## Use Stories/Visions

* E-commerce
```
 The e-commerce wants to place an order to the service.
 The e-commerce wants to ask for a shipment cost.
 The e-commerce wants to get the placed order.
 The e-commerce wants to cancel the placed order.
```

* Order Fulfiller
```
 As an order fulfiller, I would like to see all the orders.
 As an order fulfiller, I would like to see the order by order id.
 As an order fulfiller, I would like to grab the order(s).
 As an order fulfiller, I would like to fulfill the order(s).
 As an order fulfiller, I would like to revert the order(s)' status.
 As an order fulfiller, I would like to ship the order(s) to shipment service.
 As an order fulfiller, I would like to delete an order.
```

## Use Cases
* UC1: Place order
```
Stakeholder : E-commerce
Success scenario : The order is created successfully.
Description :
E-commerce places an order which contains the list of product's id and other information to the service.
The order also contains the amount which is already calculated with the shipment cost.
The payment should be success before placed to the service.
The system will create the order according to the given order.
```
* UC2: Ask for shipment cost
```
Stakeholder : E-commerce
Success scenario : The e-commerce successfully get the shipment cost.
Description : 
E-commerce send the request to get the order shipment cost. The system send the request
to ask shipment service and return back to the e-commerce.
```
* UC3: Cancel order
```
Stakeholder : E-commerce
Success scenario : The order canceled successfully.
Description :
E-commerce send the request to cancel the order. The system cancel the order.
(Note : The e-commerce will be able to cancel that order only when it is in waiting status.)
```
* UC4: See all orders
```
Stakeholder : Order fulfiller
Success scenario : The  fulfiller can view all incoming orders.
Description :
The fulfiller select view all orders. The system show all incoming orders from all e-commerces
and the status of each order to the order fulfiller.
```
* UC5: See specific order (by id)
```
Stakeholder : Order Fulfiller
Success scenario : The fulfiller can view details of each specific order by order id.
Description : 
The fulfiller select specific order. The system show the details of that order and the status of it.
```
* UC6: Grab order
```
Stakeholder : Order Fulfiller 
Success scenario : The order was grabed successfully, order's status changed to "in progress"
Description :
The order fulfiller select grab the order. The order's status changes to "in progress".
```
* UC7: Fulfill order
```
Stakeholder : Order Fulfiller 
Success scenario : The order was fulfilled(approved) successfully, order's status changed to "fulfilled"
Description :
The order fulfiller select fulfill or approve the order. The order's status changes to "fulfilled".
```
* UC8: Revert order's status
```
Stakeholder : Order Fulfiller 
Success scenario : The order's status was revert back 1 step.
Description :
The order fulfiller select undo the order. The order's status changes back 1 step before the current status.
```
* UC9: Ship order
```
Stakeholder : Order Fulfiller 
Success scenario : The order was shipped to shipment service successfully, order's status changed to "shipping"
Description :
The order fulfiller select ship the order. The system send the request to the shipment service.
If success, the order's status changes to "shipping".
```
* UC10: Delete Order
```
Stakeholder : Order Fulfiller 
Success scenario : The order is deleted successfully.
Description :
Order fulfiller select delete the specific order from the order list. The order disappear from the list. 
```
## API Document
* [Online API Document](https://docs.google.com/document/d/1L6OOY9A68hwQ-QJjaWAAZAKGnS31ZiXh1P3-_lgny4s/edit?usp=sharing)

## Design
* [Domain Design of Order](https://docs.google.com/drawings/d/1d1F-2GIwgM1IFwb965FvPROm4TsFpRhgsky5ZbVx0i4/edit?usp=sharing)
* [SSD:Make order](https://docs.google.com/drawings/d/18OjOVGN12oBs2m6lenH51n527tP2X9RfK29hU6keLUg/edit?usp=sharing)
* [SSD:Handle order](https://docs.google.com/drawings/d/1qLBuWxm7wIJXEChVLZKf9mn6xzXKYoXsWolnaYvyBQo/edit?usp=sharing)
* [SSD:Track order](https://docs.google.com/drawings/d/1n6GAvvjoSPLHm4o9bPlpngLyFRpMfCPDBoY5JCPGofk/edit?usp=sharing)
 
## Mockup
* [Order List](https://docs.google.com/drawings/d/1CE-hldA53lkfRboRw5UZ3WaPl3WOZ8NPvskR2wAJgtw/edit?usp=sharing)
* [Order Detail](https://docs.google.com/drawings/d/16bImvvNmh4kkUJ0ubP5AqpJ4yldigHQ9g-rDYrpuBj8/edit?usp=sharing)

## Out of scope
* Handle many order fulfillers. (Now we're assuming that there is only 1 order fulfiller.)
* Order Fulfiller Authentication (DONE)

## Group Member
* Eknarin Thirayothin	   5510546239
* Natcha  Chidchob 		    5510546239
* Sarathit  Sangtaweep 	 5510546182
* Natchanon Hongladaromp 5510546034

## Demo
* [Order Fulfillment website](http://128.199.175.223/fulfillment/)

## Referrences
* https://se.cpe.ku.ac.th/wiki/index.php
* http://www.fulfillmentwarehouse.biz/what-is-fulfillment.asp

