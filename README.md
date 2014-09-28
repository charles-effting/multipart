multipart
=========

An utility library to handle and validate an uploaded file. It uses a rules library provided by https://github.com/staroski/rules which makes it easier check and validate a file. For instance:

> // Create the rule to check if the file is an image.
> Rule<MultiPartFileContext> rules = Rule.create(new ImageMultiPartRule());
>
> // Validate and move the file to the target, if it satisfied by the rules.
> MultiPartFile.get(inputStream)
>			.isSatisfiedBy(rules)
>			.writeTo("/path/to/file");


