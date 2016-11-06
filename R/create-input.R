## Constraints
## 1 ≤ N ≤ 10^5
## 0 ≤ Ti ≤ 10^9
## 1 ≤ Li ≤ 10^9

library(magrittr)

n_range <- c(1, 10^4) # 10^5, number of orders
ti_range <- c(0, 10^4) # 10^9, order time
li_range <- c(1, 10^4) # 10^9, cooking time

set.seed(10)

n <- runif(n = 1, min = n_range[1], max = n_range[2]) %>% round
ti <- runif(n = n, min = ti_range[1], max = ti_range[2]) %>% round
li <- runif(n = n, min = li_range[1], max = li_range[2]) %>% round

orders <-
  data.frame(arrival = ti, cooking = li) %>%
  .[order(.[["arrival"]]), ]

orders_str <-
  paste(orders[["arrival"]], orders[["cooking"]], sep = " ")

prepend <- paste0(toString(n))

body <-
  c(prepend, orders_str)

txtfile <- (paste0("../data/scheduling-input-", n, ".txt"))
filecon <- file(txtfile)

writeLines(body, con = filecon)

close(con = filecon)
