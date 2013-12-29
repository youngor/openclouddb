/**
 * 
 */
package com.talent.nio.page;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @filename: com.talent.platform.core.pager.ListPageable
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2013-3-24 下午12:09:56
 * @record <table cellPadding="3" cellSpacing="0" style="width:600px">
 *         <thead style="font-weight:bold;background-color:#e3e197">
 *         <tr>
 *         <td>date</td>
 *         <td>author</td>
 *         <td>version</td>
 *         <td>description</td>
 *         </tr>
 *         </thead> <tbody style="background-color:#ffffeb">
 *         <tr>
 *         <td>2013-3-24</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public class ListPageable<T> extends AbstractPageable
{
    private static Logger log = LoggerFactory.getLogger(ListPageable.class);

    private List<T> allData = null;

    /**
     * 
     * @param pageSize
     * @param pageIndex
     * @param allData
     */
    public ListPageable(int _pageSize, int _pageIndex, List<T> _allData)
    {
        this.pageSize = processPageSize(_pageSize);
        this.pageIndex = processPageIndex(_pageIndex);

        this.allData = _allData;

        if (_allData != null)
        {
            this.recordCount = _allData.size();
        } else
        {
            this.recordCount = 0;
        }

        if (this.isPagination)
        {
            long pageCount = calculatePageCount(this.pageSize, recordCount);
            if (this.pageIndex > pageCount)
            {
                this.pageIndex = pageCount;
            }
            if (this.pageIndex <= 0)
            {
                this.pageIndex = 1;
            }

            List<Object> data1 = new ArrayList<Object>();
            int startIndex = (int) (this.pageIndex - 1) * this.pageSize;
            int endIndex = (int) (this.pageIndex) * this.pageSize;

            if (this.allData == null)
            {
                endIndex = 0;
            } else if (endIndex > allData.size())
            {
                endIndex = allData.size();
            }

            for (int i = startIndex; i < endIndex; i++)
            {
                data1.add(allData.get(i));
            }
            this.data = data1;
        } else
        {
            this.data = allData;
        }
    }

    /**
     * 
     * @param pageSize
     * @param pageIndex
     * @param recordCount
     * @param currentPageData
     */
    public ListPageable(int pageSize, int pageIndex, long recordCount, List<T> currentPageData)
    {
        this.pageSize = processPageSize(pageSize);
        this.pageIndex = processPageIndex(pageIndex);
        this.recordCount = recordCount;
        this.data = currentPageData;
    }

    @Override
    public void reload()
    {
        // final List<Integer> piDigits = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5, 9};
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
    }

}
