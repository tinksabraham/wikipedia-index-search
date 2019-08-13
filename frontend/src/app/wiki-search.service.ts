import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { WikiSearchModel } from './wiki-search.model';

@Injectable()
export class WikiSearchService {
  searchUrl = 'search?term=${searchTerm}&size=${searchSize}';

  constructor(private http: HttpClient) { }

  getWikiArticles(searchTerm: string, searchSize: string): Observable<WikiSearchModel[]> {
    this.searchUrl = this.searchUrl.replace('${searchTerm}', searchTerm);
    this.searchUrl = this.searchUrl.replace('${searchSize}', searchSize);
    return this.http.get<WikiSearchModel[]>(this.searchUrl);
  }

}
