# Order Fulfillment Service
 Our service is made for receiving orders which come from the e-commerce, keep those order, wait to be fulfilled and also track those order until they are fulfilled or approved and the e-commerce can check their orders' status. 
 This service requires other services to succeed its goal such as Product Service from the e-commerce.

## What is Order Fulfillment ?
* Order fulfillment is the complete process from point of sales inquiry to delivery of a product to the customer. Sometimes Order Fulfillment is used to simply describe the act of distribution (logistics) or the shipping function, but in the broader sense it refers to the way firms respond to customer orders, and the process they take to move products from those orders, to the customer.
* In simpler terms, Order Fulfillment is everything that a seller does from the moment an order for a product is received, to the customer having their purchase in their hands. Order Fulfillment also includes the processes involved in receiving products to sell, storing those products, and providing inventory control of those products.
* From http://www.fulfillmentwarehouse.biz/what-is-fulfillment.asp

## Stakeholder
* E-Commerce
* Order Fulfiller

## Use Stories/Visions

* E-commerce
```
 The e-commerce wants to place an order to the service.
 The e-commerce wants to get the placed order.
 The e-commerce wants to update the placed order.
 The e-commerce wants to cancel the placed order.
```

* Order Fulfiller
```
 As an order fulfiller, I would like to see all the orders.
 As an order fulfiller, I would like to see the order by order id.
 As an order fulfiller, I would like to fulfill the order(s).
 As an order fulfiller, I would like to update the order's status.
 As an order fulfiller, I would like to delete an order.
```

## Use Cases
* UC1: Place order
```
Stakeholder : E-commerce
Success scenario : The order is created successfully.
Description :
E-commerce places an order which contains the list of product's id and other information to the service.
The system creates the order according to the given order.
```
* UC2: See all orders
```
Stakeholder : Order fulfiller
Success scenario : The  fulfiller can view all incoming orders.
Description :
The fulfiller select view all orders. The system show all incoming orders from all e-commerces
and the status of each order to the order fulfiller.
```
* UC3: See specific order (by id)
```
Stakeholder : Order Fulfiller
Success scenario : The fulfiller can view details of each specific order by order id.
Description : 
The fulfiller select specific order. The system show the details of that order and the status of it.
```
* UC4: Update order
```
Stakeholder : E-commerce
Success scenario : The order updated successfully.
Description : 
E-commerce send the request to update the order. The system update the order.
(Note : The e-commerce will be able to update that order only when it is in waiting status.)
```
* UC5: Cancel order
```
Stakeholder : E-commerce
Success scenario : The order canceled successfully.
Description :
E-commerce send the request to cancel the order. The system cancel the order.
(Note : The e-commerce will be able to cancel that order only when it is in waiting status.)
```
* UC6: Fulfill order
```
Stakeholder : Order Fulfiller 
Success scenario : The order was fulfilled(approved) successfully, order's status changed to "Fulfilled"
Description :
The order fulfiller select fulfill or approve the order. The order's status changes to "Fulfilled".
```
* UC7: Get order’s status 
```
Stakeholder : E-commerce
Success scenario : The order's status responses to the e-commerce.
Description :
E-commerce send the request to get the order's status. The system response the status back.
```
* UC8: Delete Order
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
* [SSD:Place order](https://docs.google.com/drawings/d/1XZY_FO9gRhPT2xQOgvdKUU1Gy6yEnUnTmgivQnZfEsQ/edit?usp=sharing)
* [SSD:Get order status](https://docs.google.com/drawings/d/1U-1y4WC9OOypi7_ME-ieggCHqoAw2igp735n1NtPqyY/edit?usp=sharing)
* [SSD:Update Order](https://docs.google.com/drawings/d/12zj5KmVS9yWLgFNWEQY1IOlcXtSIw07xulZCbQDIIdY/edit?usp=sharing)
 
## Mockup
--- Coming soon ---

## Out of scope
* Handle many order fulfillers. (Now we're assuming that there is only 1 order fulfiller.)
* Order Fulfiller Authentication

## Group Member
* Eknarin Thirayothin	   5510546239
* Natcha  Chidchob 		    5510546239
* Sarathit  Sangtaweep 	 5510546182
* Natchanon Hongladaromp 5510546034

## Referrences
* https://se.cpe.ku.ac.th/wiki/index.php
* http://www.fulfillmentwarehouse.biz/what-is-fulfillment.asp

