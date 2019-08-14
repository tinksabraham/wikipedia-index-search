import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material';
import { WikiSearchModel } from './wiki-search.model';
import { WikiSearchService } from './wiki-search.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'AID Wikipedia Search';
  displayedColumns: string[] = ['Wikipedia Id', 'Contributor', 'Article'];
  wikiSearchList = new MatTableDataSource<WikiSearchModel[]>();
  selected = "10"; // default size
  search = "";
  isLoaded = true;


  constructor (private wikiSearchService: WikiSearchService) { }

  ngOnInit() {
  }

  onSearch() {
    if (this.search) {
      this.isLoaded = false;
      this.wikiSearchService.getWikiArticles(this.search, this.selected).subscribe((response) => {

        this.wikiSearchList = response as any;
        this.isLoaded = true;

      }, error => {
        // on error it should hide
        this.isLoaded = true;
      });
    }


  }
}
