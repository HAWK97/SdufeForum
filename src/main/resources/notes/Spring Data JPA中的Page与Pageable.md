Page是Spring Data JPA提供的一个接口，该接口表示一部分数据的集合以及其相关的下一部分数据、数据总数等相关信息。通过该接口，我们可以得到数据的总体信息（数据总数、总页数等）以及当前数据的信息（当前数据的集合、当前页数等）。  
Page接口如下：
```
public interface Page<T> extends Iterable<T> {  
  
    int getNumber();             //当前第几页 返回当前页的数目，总是非负的。  
  
    int getSize();               //返回当前页面的大小。  
  
    int getTotalPages();         //返回分页总数。  
  
    int getNumberOfElements();   //返回当前页上的元素数。  
  
    long getTotalElements();     //返回元素总数。  
  
    boolean hasPreviousPage();   //返回如果有上一页。  
  
    boolean isFirstPage();       //返回当前页是否为第一页。  
  
    boolean hasNextPage();       //返回如果有下一页。  
  
    boolean isLastPage();        //返回当前页是否为最后一页。  
  
    Iterator<T> iterator();  
  
    List<T> getContent();        //将所有数据返回为List  
  
    boolean hasContent();        //返回数据是否有内容。  
  
    Sort getSort();              //返回页的排序参数。  
```
Pageable是Spring Data JPA库中定义的一个接口，该接口是所有分页相关信息的一个抽象，通过该接口，我们可以得到和分页相关所有信息（例如pageNumber、pageSize等），这样，JPA就能够通过Pageable参数来得到一个带分页信息的SQL语句。  
Pageable定义了很多方法，但其核心的信息只有两个：一是分页的信息（page、size），二是排序的信息。Spring Data JPA提供了PageRequest的具体实现。