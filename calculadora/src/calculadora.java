import java.util.*;


public class calculadora {
    public static void main(String[] args) {

                Scanner scanner = new Scanner(System.in);
                while (true) {
                    System.out.println("Introduce una expresión aritmética en notación infija");
                    System.out.print("> ");
                    String inputString = scanner.nextLine();
                    if (inputString.equalsIgnoreCase("quit")) {
                        break;
                    }
                    try {
                        double result = evaluate(inputString);
                        System.out.printf("Resultado: %.2f%n", result);
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
            }

            public static double evaluate(String input) {
                List<String> tokens = getTokens(input);
                if (!isValidExpression(tokens)) {
                    throw new IllegalArgumentException("Invalid expression");
                }
                List<String> postfix = toPostfix(tokens);
                return evaluatePostfix(postfix);
            }

            public static List<String> getTokens(String input) {
                List<String> tokenList = new ArrayList<>();
                StringBuilder tokenBuilder = new StringBuilder();

                for (char c : input.toCharArray()) {
                    if (Character.isDigit(c) || c == '.') {
                        tokenBuilder.append(c);
                    } else if (isOperator(String.valueOf(c))) {
                        if (tokenBuilder.length() > 0) {
                            tokenList.add(tokenBuilder.toString());
                            tokenBuilder.setLength(0);
                        }
                        tokenList.add(String.valueOf(c));
                    } else if (c == ' ') {
                        if (tokenBuilder.length() > 0) {
                            tokenList.add(tokenBuilder.toString());
                            tokenBuilder.setLength(0);
                        }
                    } else if (c == '(' || c == ')') {
                        if (tokenBuilder.length() > 0) {
                            tokenList.add(tokenBuilder.toString());
                            tokenBuilder.setLength(0);
                        }
                        tokenList.add(String.valueOf(c));
                    } else {
                        throw new IllegalArgumentException("Invalid character: " + c);
                    }
                }

                if (tokenBuilder.length() > 0) {
                    tokenList.add(tokenBuilder.toString());
                }

                return tokenList;
            }

            public static boolean isValidExpression(List<String> tokens) {
                int parenthesesCount = 0;

                for (String token : tokens) {
                    if (token.equals("(")) {
                        parenthesesCount++;
                    } else if (token.equals(")")) {
                        if (parenthesesCount == 0) {
                            return false;
                        }
                        parenthesesCount--;
                    }
                }

                return parenthesesCount == 0;
            }

            public static List<String> toPostfix(List<String> tokens) {
                Deque<String> stack = new ArrayDeque<>();
                List<String> output = new ArrayList<>();

                for (String token : tokens) {
                    if (token.equalsIgnoreCase("(")) {
                        stack.push(token);
                    } else if (token.equalsIgnoreCase(")")) {
                        while (!stack.peek().equals("(")) {
                            output.add(stack.pop());
                        }
                        stack.pop(); // Remove the '('
                    } else if (isOperand(token)) {
                        output.add(token);
                    } else if (isOperator(token)) {
                        while (!stack.isEmpty() && !stack.peek().equals("(") && getPrec(token) <= getPrec(stack.peek())) {
                            output.add(stack.pop());
                        }
                        stack.push(token);
                    }
                }

                while (!stack.isEmpty()) {
                    output.add(stack.pop());
                }

                return output;
            }

            public static double evaluatePostfix(List<String> postfix) {
                Deque<Double> stack = new ArrayDeque<>();

                for (String token : postfix) {
                    if (isOperand(token)) {
                        stack.push(Double.parseDouble(token));
                    } else if (isOperator(token)) {
                        double operand2 = stack.pop();
                        double operand1 = stack.pop();
                        double result = applyOperator(token, operand1, operand2);
                        stack.push(result);
                    }
                }

                return stack.pop();
            }

            public static double applyOperator(String operator, double operand1, double operand2) {
                switch (operator) {
                    case "+":
                        return operand1 + operand2;
                    case "-":
                        return operand1 - operand2;
                    case "*":
                        return operand1 * operand2;
                    case "/":
                        if (operand2 == 0) {
                            throw new ArithmeticException("Division by zero");
                        }
                        return operand1 / operand2;
                    case "^":
                        return Math.pow(operand1, operand2);
                    default:
                        throw new UnsupportedOperationException("Unknown operator: " + operator);
                }
            }

            public static boolean isOperator(String token) {
                return token.equals("+") || token.equals("-") ||
                        token.equals("*") || token.equals("/") || token.equals("^");
            }

            public static boolean isOperand(String token) {
                try {
                    Double.parseDouble(token);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            public static int getPrec(String token) {
                switch (token) {
                    case "^":
                        return 3;
                    case "*":
                    case "/":
                        return 2;
                    case "+":
                    case "-":
                        return 1;
                    default:
                        return 0;
                }
            }
        }

