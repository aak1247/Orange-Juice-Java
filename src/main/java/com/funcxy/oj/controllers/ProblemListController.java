package com.funcxy.oj.controllers;

import com.funcxy.oj.errors.BadRequestError;
import com.funcxy.oj.errors.ForbiddenError;
import com.funcxy.oj.models.ProblemList;
import com.funcxy.oj.models.User;
import com.funcxy.oj.repositories.ProblemListRepository;
import com.funcxy.oj.repositories.UserRepository;
import com.funcxy.oj.utils.DataPageable;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;

import static com.funcxy.oj.utils.UserUtil.isSignedIn;

/**
 * Created by wtupc96 on 2017/3/4.
 *
 * @author Peter
 * @version 1.0
 */

@RestController
@RequestMapping("/problemLists")
public class ProblemListController {
    private static final Sort sort = new Sort(Sort.Direction.ASC, "title");

    @Autowired
    private ProblemListRepository problemListRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;

    private DataPageable pageable;

    {
        pageable = new DataPageable();
        pageable.setSort(sort);
    }

    @RequestMapping(value = "/problemListsOwned", method = RequestMethod.GET)
    public ResponseEntity getProblemListsOwned(@RequestParam int pageNumber,
                                               @RequestParam int pageSize,
                                               HttpSession session) {
        if (!isSignedIn(session)) {
            return new ResponseEntity(new ForbiddenError(), HttpStatus.FORBIDDEN);
        }

        pageable.setPageSize(pageSize);
        pageable.setPageNumber(pageNumber);
        // 后端题单检索功能，版权所有，请勿删除。
//        if (creator != null && title != null) {
//            return new ResponseEntity(problemListRepository.findByCreatorLikeAndTitleLike(creator, title, pageable), HttpStatus.OK);
//        } else if (creator != null) {
//            return new ResponseEntity(problemListRepository.findByCreatorLike(creator, pageable), HttpStatus.OK);
//        } else if (title != null) {
//            return new ResponseEntity(problemListRepository.findByTitleLike(title, pageable), HttpStatus.OK);
//        } else {
//            return new ResponseEntity(problemListRepository.findAll(pageable), HttpStatus.OK);
//        }
        return new ResponseEntity(problemListRepository
                .getAllProblemListsCreated(
                        new ObjectId(session
                                .getAttribute("userId")
                                .toString()), pageable), HttpStatus.OK);
    }

    @RequestMapping(value = "/problemListsIn", method = RequestMethod.GET)
    public ResponseEntity getProblemLists(@RequestParam int pageNumber,
                                          @RequestParam int pageSize,
                                          HttpSession session) {
        if (!isSignedIn(session)) {
            return new ResponseEntity(new ForbiddenError(), HttpStatus.FORBIDDEN);
        }
        pageable.setPageSize(pageSize);
        pageable.setPageNumber(pageNumber);

        return new ResponseEntity(problemListRepository
                .findByUserListLike(
                        new ObjectId(session
                                .getAttribute("userId")
                                .toString()), pageable), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity getOneSpecificProblemList(@PathVariable ObjectId id,
                                                    HttpSession session) {
        if (!isSignedIn(session)) {
            return new ResponseEntity(new ForbiddenError(), HttpStatus.FORBIDDEN);
        }

        ProblemList tempProblemList = problemListRepository.findById(id);

        if (tempProblemList == null) {
            return new ResponseEntity(new BadRequestError(), HttpStatus.BAD_REQUEST);
        }

        if (tempProblemList.isAccessible() ||
                tempProblemList
                        .getUserList()
                        .contains(new ObjectId(
                                session.getAttribute("userId")
                                        .toString()))) {
            Date now = new Date(System.currentTimeMillis());
            if (tempProblemList.getReadEndTime().before(now) &&
                    tempProblemList.getReadEndTime().after(now)) {
                return new ResponseEntity(tempProblemList, HttpStatus.OK);
            } else
                return new ResponseEntity(new BadRequestError(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(new ForbiddenError(), HttpStatus.FORBIDDEN);
    }


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createProblemList(@Valid @RequestBody ProblemList problemList,
                                            HttpSession session) {
        if (!isSignedIn(session)) {
            return new ResponseEntity(new ForbiddenError(), HttpStatus.FORBIDDEN);
        }

        if (!problemList.isAccessible()) {
            problemList.setUserList(null);
        }

        ObjectId tempObjectId = new ObjectId(session.getAttribute("userId").toString());

        problemList.setCreator(tempObjectId);
        problemList.setCreatedTime(new Date());
        ProblemList tempProblemList = problemListRepository.save(problemList);

        User user = userRepository.findById(tempObjectId);
        user.addProblemListOwned(tempProblemList.getId());
        userRepository.save(user);

        return new ResponseEntity(tempProblemList, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity modifyProblemList(@RequestBody @Valid ProblemList problemList,
                                            @PathVariable ObjectId id,
                                            HttpSession session) {
        ObjectId tempObjectId = new ObjectId(session.getAttribute("userId").toString());

        if (!(isSignedIn(session)
                && tempObjectId
                .equals(problemListRepository.findById(id).getCreator()))) {
            return new ResponseEntity(new ForbiddenError(), HttpStatus.FORBIDDEN);
        }

        if (!problemList.isAccessible()) {
            problemList.setUserList(null);
        }

        problemList.setId(id);
        problemList.setCreator(tempObjectId);

        return new ResponseEntity(problemListRepository.save(problemList), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteProblemList(@PathVariable ObjectId id,
                                            HttpSession session) {
        ProblemList tempProblemList = problemListRepository.findById(id);

        ObjectId tempObjectId = new ObjectId(session.getAttribute("userId").toString());

        if (!(isSignedIn(session)
                && tempObjectId
                .equals(tempProblemList
                        .getCreator()))) {
            return new ResponseEntity(new ForbiddenError(), HttpStatus.FORBIDDEN);
        }

        problemListRepository.delete(tempProblemList);

        User user = userRepository.findById(tempObjectId);
        user.deleteProblemListOwned(tempProblemList.getId());
        userRepository.save(user);

        return new ResponseEntity(tempProblemList, HttpStatus.OK);
    }
}