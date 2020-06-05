# Json Entity Blueprint (JEB)

The JEB library is intended to aid in the creation Java REST services within the Spring framework.  Many Spring projects that implement REST services require a glue layer to convert business entities stored in a relational database into simple java objects that can be easily serialized into Json responses via Jackson.  This project aims to make that glue layer unnecessary.

## Justification

Many Spring projects that use REST services end up writing a bunch of boilerplate POJO's simply for the purpose of making serialization to JSON easier.  The pattern usually goes like this: load the domain objects (or "entities" as JPA calls them) from the database with JPA / Hibernate, then pick and choose the data needed for the UI and copy it all over into a custom object model that then gets handed over to a framework like Jackson for serialization into a JSON response.

This seems redundant since many of our domain classes so often closely resemble the JSON responses we are looking to produce.  Why not simply pass our domain entities to Jackson?  Well, there are at least two problems with this:

1. First of all, Jackson will traverse an arbitrarily complex object graph unless you tell it where to stop.  So in this case, it could cause Hibernate to load the entire database into memory.

1. Since it attempts to follow all references, Jackson will spin its wheels on back references like an invoice that has a list of line items, where each line item has a reference back to the parent invoice.  This causes Jackson (at least by default) to go back and re-process the parent, which re-processes the line items until it runs out of stack space.

### But, we have options like @JsonIgnore, @JsonManagedReference, and @JsonBackreference for this sort of thing, right?

Well, yes, but there are two main problems with using these in this type of situation.  First of all, in order to use these annotations you must obviously have control of the source code for the classes you are serializing.  If the reference you want Jackson to ignore happens to reside in a third-party library, then none of these annotations will be an option.

Secondly, these solutions don't work well in a REST application where you may be returning different views of the same object depending on which service is being invoked.  Take the simple example of a `Department` that has a `List` of `Employees`, where each `Employee` has a back reference to the `Department`.  You could avoid the infinite loop by adding `@JsonManagedReference` to the `List` and annotating the `Department` field in `Employee` with `@JsonBackreference` as you are only serializing `Department`.  However, if you want to serialize an `Employee`, then Jackson will only serialize the `Employee` fields.  It will not follow the `Department` reference since it has been annotated with `@JsonBackReference`, which may or may not be desired.

Another example: let's say you want one web service to return a summary view of `Department` with just its fields and no `Employee`s.  You could use `@JsonIgnore` on the `List` of `Employees`, but if you then wanted a second, detailed view of `Department` where the `Employee`s **were** included, there would be no way to accomplish this.

### What about @JsonViews?



## Examples

Coming soon...

## Configuring For The Spring Framework

Coming soon...

## License

This project is licensed under the MIT License
