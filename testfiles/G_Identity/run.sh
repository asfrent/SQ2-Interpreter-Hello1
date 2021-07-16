#!/bin/bash
java -jar Sq2_Interpreter_Hello.jar test.sq2 test.lir
lli test.lir < in &> out
